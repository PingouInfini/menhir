#!/bin/bash

#Paths used in this script
CURRENT_PATH=$(pwd)
WORKING_DIR=$(dirname "$0")

# use sudo command to force user to set his password at the beginning of the script
sudo ls >/dev/null 2>&1

reboot_is_needed=false


apt_install_package(){
  sudo apt install -y "$1" 2>/dev/null | grep packages | cut -d '.' -f 1
}

check_if_package_installed () {
    PKG_INSTALLED=$(dpkg-query -W --showformat='${Status}\n' "$1")
    if [ "$PKG_INSTALLED" != "install ok installed" ]; then
        apt_install_package "$1"
    fi
}

# Installation de docker, avec version en parametre $1
install_docker() {
	sudo apt-get update 2>/dev/null | grep packages | cut -d '.' -f 1
	apt_install_package apt-transport-https
	apt_install_package ca-certificates
	apt_install_package curl
	apt_install_package software-properties-common
	#add Docker's offical GPG key
	curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
	#set stable repository
	sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
	#install docker-ce
	sudo apt-get update 2>/dev/null | grep packages | cut -d '.' -f 1
	apt_install_package docker-ce

	#set daemon to expose interface on 2375 and enable ipv6 routing to containers
	echo '{
	"debug": true,
	"ipv6": true,
	"fixed-cidr-v6": "2001:db8:1::/64",
	"hosts": ["unix:///var/run/docker.sock", "tcp://127.0.0.1:2375"]
	}' | sudo tee -a /etc/docker/daemon.json 2>/dev/null

	sudo mkdir -p /etc/systemd/system/docker.service.d

	echo '[Service]
	ExecStart=
	ExecStart=/usr/bin/dockerd' | sudo tee -a /etc/systemd/system/docker.service.d/docker.conf 2>/dev/null


	sudo systemctl daemon-reload
	sudo systemctl restart docker

	#install docker-compose
	sudo curl -L "https://github.com/docker/compose/releases/download/""$1""/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose 2>/dev/null
	sudo chmod +x /usr/local/bin/docker-compose
	sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose

	#add current user to docker group
	sudo usermod -aG docker "$USER"

	reboot_is_needed=true
}

ask_for_reboot(){
  read -r -p "Etes vous sur de vous redemarrerr? [y/N] " response
case "$response" in
    [yY][eE][sS]|[yY])
        echo "reboot ..."
        sleep 2
        sudo reboot
        ;;
    *)
        :
        ;;
esac
}

rebuild_images(){
	check_if_package_installed git
		
	echo ""
	echo $(date +%H:%M:%S)   "INFO   Build des images"
	date=$(date +"%Y-%m-%d_%H-%M-%S")
	mkdir -p /tmp/rebuild/$date
	
	HEIGHT=15
	WIDTH=40
	CHOICE_HEIGHT=4

	TITLE="Image builder"
	MENU="Choix des images à installer ('espace' pour faire un choix, 'entree' pour valider) :"

	CHOIX=$(whiptail --title "$TITLE" --radiolist \
	"$MENU" "$HEIGHT" "$WIDTH" "$CHOICE_HEIGHT" \
	"00" "Aucune" ON \
	"01" "Toutes" OFF \
	"02" "Menhir" OFF \
	"03" "Alesia" OFF \
	3>&1 1>&2 2>&3)
	
	cd /tmp/rebuild/$date

	case $CHOIX in
		*00*)
			echo $(date +%H:%M:%S)   "DEBUG  aucune image selectionnee"
			;;&
		*01*)
			stop_containers "pingouinfinihub/menhir pingouinfinihub/alesia"
			build_push_new_image menhir
			build_push_new_image alesia
			;;&
		*02*)
			stop_containers "pingouinfinihub/menhir"
			build_push_new_image menhir
			;;&
		*03*)
			stop_containers "pingouinfinihub/alesia"
			build_push_new_image alesia
			;;&
	esac
	
	cd $CURRENT_PATH
	rm -rf /tmp/rebuild/$date
}

build_push_new_image() {
	app=$1
	echo $(date +%H:%M:%S)   "DEBUG  build $app"
	git clone https://github.com/PingouInfini/$app.git
	cd $app
	./mvnw -DskipTests -Pprod verify jib:dockerBuild
	cd ..

	echo ""
	echo $(date +%H:%M:%S)   "DEBUG  push $app"

	docker login --username pingouinfinihub -p '489eb399-fe2f-44af-80f4-a0c81acd71a5'
	docker rmi pingouinfinihub/$app
	docker tag $app pingouinfinihub/$app:latest
	docker push pingouinfinihub/$app:latest
	docker rmi $app
}

preparation_images(){
	mkdir -p $WORKING_DIR/images
	rm -rf $WORKING_DIR/images/*
	
	cd $WORKING_DIR
	
	dockerImages="pingouinfinihub/menhir pingouinfinihub/alesia postgres:12.3"
	stop_containers $dockerImages

	echo ""
	echo $(date +%H:%M:%S)   "INFO   Pull des images depuis dockerhub"
	for img in $dockerImages; do
		docker pull $img
	done

	echo ""
	echo $(date +%H:%M:%S)   "INFO   Sauvegarde des images au format tar dans le répertoire $(pwd)/images"
	for img in $dockerImages; do
		imgNameTmp=$(echo $img| cut -d'/' -f 2)
		imgName=$(echo $imgNameTmp| cut -d':' -f 1)
		echo $(date +%H:%M:%S)   "DEBUG  Creation de $WORKING_DIR/images/$imgName.tar"
		docker save -o  $WORKING_DIR/images/$imgName.tar $img
	done
	cd $CURRENT_PATH
}

preparation_container() {

	mkdir -p $WORKING_DIR/docker
	rm -rf $WORKING_DIR/docker/*
	
	NBCONTAINERS="$1"
	echo ""
	echo $(date +%H:%M:%S)   "INFO   Preparation des $NBCONTAINERS container(s)"
				
				
	 # Preparer les yml pour lancer les BDD et les $NBCONTAINERS
	 echo "version: '2'
services:
  menhir-postgresql:
    image: postgres:12.3
    #volumes:
    #  - ~/volumes/jhipster/Menhir/postgresql/:/var/lib/postgresql/data/
    environment:
      - POSTGRES_USER=Menhir
      - POSTGRES_PASSWORD=
      - POSTGRES_HOST_AUTH_METHOD=trust
    ports:
      - 5432:5432" | tee $WORKING_DIR/docker/menhir-postgresql.yml >/dev/null

	echo "version: '2'
services:
  alesia-postgresql:
    image: postgres:12.3
    #volumes:
    #  - ~/volumes/jhipster/Alesia/postgresql/:/var/lib/postgresql/data/
    environment:
      - POSTGRES_USER=Alesia
      - POSTGRES_PASSWORD=
      - POSTGRES_HOST_AUTH_METHOD=trust
    ports:
      - 5433:5433" | tee $WORKING_DIR/docker/alesia-postgresql.yml >/dev/null

	for (( nb=1; nb<=$NBCONTAINERS; nb++ ))
	do
		portCible=$((18080 + $nb))				 
		echo "version: '2'
services:
  menhir-$nb-app:
    image: pingouinfinihub/menhir:latest
    environment:
      - _JAVA_OPTIONS=-Xmx512m -Xms256m
      - SPRING_PROFILES_ACTIVE=prod,swagger
      - MANAGEMENT_METRICS_EXPORT_PROMETHEUS_ENABLED=true
      - SPRING_DATASOURCE_URL=jdbc:postgresql://menhir-postgresql:5432/Menhir$nb
      - JHIPSTER_SLEEP=30 # gives time for other services to boot before the application
    ports:
      - $portCible:18080" | tee $WORKING_DIR/docker/menhir-$nb-app.yml >/dev/null
	done
	
	for (( nb=1; nb<=$NBCONTAINERS; nb++ ))
	do
		portCible=$((28080 + $nb))				 
		echo "version: '2'
services:
  alesia-$nb-app:
    image: pingouinfinihub/alesia:latest
    environment:
      - _JAVA_OPTIONS=-Xmx512m -Xms256m
      - SPRING_PROFILES_ACTIVE=prod,swagger
      - MANAGEMENT_METRICS_EXPORT_PROMETHEUS_ENABLED=true
      - SPRING_DATASOURCE_URL=jdbc:postgresql://alesia-postgresql:5432/Alesia$nb
      - JHIPSTER_SLEEP=30 # gives time for other services to boot before the application
    ports:
      - $portCible:28080" | tee $WORKING_DIR/docker/alesia-$nb-app.yml >/dev/null
	done
	
	for (( nb=1; nb<=$NBCONTAINERS; nb++ )); do
		echo "CREATE DATABASE \"Menhir$nb\" WITH OWNER = \"Menhir\" ENCODING = 'UTF8' TABLESPACE = pg_default CONNECTION LIMIT = -1;" | tee -a  $WORKING_DIR/docker/menhir-initdatabase.sql >/dev/null
	done
	
	for (( nb=1; nb<=$NBCONTAINERS; nb++ )); do
		echo "CREATE DATABASE \"Alesia$nb\" WITH OWNER = \"Alesia\" ENCODING = 'UTF8' TABLESPACE = pg_default CONNECTION LIMIT = -1;" | tee -a  $WORKING_DIR/docker/alesia-initdatabase.sql >/dev/null
	done
}

launch_containers() {
	
	nbYml=$(ls docker/$1*app.yml | wc -l)
	for (( i=1; i<=$nbYml; i++ )); do
		echo $(date +%H:%M:%S)   "DEBUG  init $1-$i-app"
		docker-compose -f docker/$1-$i-app.yml up -d
	done
}

load_images(){
	dockerImages="pingouinfinihub/menhir pingouinfinihub/alesia postgres:12.3"
	
	echo ""
	echo $(date +%H:%M:%S)   "INFO   Load des images"
	
	for img in $dockerImages; do
		docker rmi $img
	
		imgNameTmp=$(echo $img| cut -d'/' -f 2)
		imgName=$(echo $imgNameTmp| cut -d':' -f 1)
		echo $(date +%H:%M:%S)   "DEBUG  loading $WORKING_DIR/images/$imgName.tar"
		docker load -i $WORKING_DIR/images/$imgName.tar
	done
}

init_containers() {

	echo ""
	echo $(date +%H:%M:%S)   "INFO   Initialisation des containers"
	echo $(date +%H:%M:%S)   "DEBUG  init menhir-postgresql"
	docker-compose -f docker/menhir-postgresql.yml up --no-start && docker cp docker/menhir-initdatabase.sql docker_menhir-postgresql_1:/docker-entrypoint-initdb.d/menhir-initdatabase.sql && docker-compose -f docker/menhir-postgresql.yml start
	echo $(date +%H:%M:%S)   "DEBUG  init alesia-postgresql"
	docker-compose -f docker/alesia-postgresql.yml up --no-start && docker cp docker/alesia-initdatabase.sql docker_alesia-postgresql_1:/docker-entrypoint-initdb.d/alesia-initdatabase.sql && docker-compose -f docker/alesia-postgresql.yml start
	
	launch_containers menhir
	launch_containers alesia
}

stop_containers() {
	echo ""
	echo $(date +%H:%M:%S)   "INFO   Arrêt et suppression des containers existants"
	for img in $1; do
		# si un container a ete lancer depuis une image, on l'arrete et delete le container
		idsToDelete=$(docker ps -a -q --filter ancestor=$img --format="{{.ID}}")
		nbmax=$(docker ps -a -q --filter ancestor=$img --format="{{.ID}}"|wc -l)
		
		for (( i=1; i<=$nbmax; i++ )); do
			idToDelete=$(echo $idsToDelete| cut -d' ' -f $i)
			docker rm $(docker stop $idToDelete)
		done
		docker container prune -f
	done
}

launch_easy_install() {

	HEIGHT=15
	WIDTH=150
	CHOICE_HEIGHT=8

	TITLE="Demo PESR easy deploy"
	MENU="Choix de l action a realiser"

	CHOIX=$(whiptail --title "$TITLE" --menu \
	"$MENU" "$HEIGHT" "$WIDTH" "$CHOICE_HEIGHT" \
	"01 Preparation" "     Recuperer les images docker depuis dockerhub et prepare les fichiers yml"  \
	"  |_step_1-1" "     ...(optionnel) Rebuild/push les images docker"  \
	"  |_step_1-2" "     ...Recuperer les images docker"  \
	"  |_step_1-3" "     ...Preparer les yml"  \
	"02 Installation" "     Load les images docker et instancie les containers"  \
	"  |_step_2-1" "     ...Load les images docker"  \
	"  |_step_2-2" "     ...Instancier les containers"  \
	"  |_step_2-3" "     ...(optionnel) Stopper les containers"  \
	3>&1 1>&2 2>&3)

	case $CHOIX in
			*Preparation*)
				NBCONTAINERS=$(whiptail --inputbox "Combien de containers seront instancies ?" 8 39 1 --title "Nb containers" 3>&1 1>&2 2>&3)
			
				preparation_images			
				preparation_container $NBCONTAINERS
	
				echo ""
				echo $(date +%H:%M:%S)   "INFO    Termine"
				exit
				;;&
				
			 *1-1*)
				rebuild_images
				
				echo ""
				echo $(date +%H:%M:%S)   "INFO    Termine"
				exit
				;;&
				
			 *1-2*)
				preparation_images
				
				echo ""
				echo $(date +%H:%M:%S)   "INFO    Termine"
				exit
				;;&
				
			*1-3*)
				NBCONTAINERS=$(whiptail --inputbox "Combien de containers seront instancies ?" 8 39 1 --title "Nb containers" 3>&1 1>&2 2>&3)
					
				preparation_container $NBCONTAINERS
				echo ""
				echo $(date +%H:%M:%S)   "INFO    Termine"
				exit
				;;&

			 *02*)
				stop_containers "pingouinfinihub/menhir pingouinfinihub/alesia postgres:12.3"
				load_images
				init_containers
			 
				echo ""
				echo $(date +%H:%M:%S)   "INFO    Termine"
				exit
				;;&
				
			 *2-1*)
				stop_containers "pingouinfinihub/menhir pingouinfinihub/alesia postgres:12.3"
				load_images
				
				echo ""
				echo $(date +%H:%M:%S)   "INFO    Termine"
				exit
				;;&
				
			*2-2*)
				stop_containers "pingouinfinihub/menhir pingouinfinihub/alesia postgres:12.3"
				init_containers
				
				echo ""
				echo $(date +%H:%M:%S)   "INFO    Termine"
				exit
				;;&
				
			*2-3*)
				stop_containers "pingouinfinihub/menhir pingouinfinihub/alesia postgres:12.3"
				
				echo ""
				echo $(date +%H:%M:%S)   "INFO    Termine"
				exit
				;;&
	esac 
}



check_if_package_installed whiptail
if [[ "$(docker -v)" =~ "Docker version" ]]
then
	echo $(date +%H:%M:%S)   'DEBUG  '$(docker -v)''
else
	DOCKER_RELEASE=1.28.2
	echo $(date +%H:%M:%S)   'INFO   Installation de Docker ...'
	install_docker $DOCKER_RELEASE
fi
launch_easy_install


if [ "$reboot_is_needed" = true ] ; then
    echo $(date +%H:%M:%S)   "WARNING   Un redémarrage est nécessaire pour finaliser l'installation"
    ask_for_reboot
fi