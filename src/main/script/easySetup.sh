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

launch_easy_install() {

	HEIGHT=15
	WIDTH=90
	CHOICE_HEIGHT=2

	TITLE="Demo PESR easy deploy"
	MENU="Choix de l action a realiser"

	CHOIX=$(whiptail --title "$TITLE" --radiolist \
	"$MENU" "$HEIGHT" "$WIDTH" "$CHOICE_HEIGHT" \
	"01" "Preparation: Recuperer les images docker depuis dockerhub" ON \
	"02" "Installation : Load les images docker et instancie les containers" OFF \
	3>&1 1>&2 2>&3)

	case $CHOIX in
			*01*)
			
				NBCONTAINERS=$(whiptail --inputbox "Combien de containers seront instancies ?" 8 39 1 --title "Nb containers" 3>&1 1>&2 2>&3)
			
				dockerImages="pingouinfinihub/menhir pingouinfinihub/alesia"
				mkdir -p $WORKING_DIR/images
				rm -rf $WORKING_DIR/images/*
				cd $WORKING_DIR
				
				echo ""
				echo $(date +%H:%M:%S)   "INFO   Suppression des images existantes (stop les containers si besoin)"
				for img in $dockerImages; do
					# si un container a ete lancer depuis une image, on l'arrete et delete le container
					if [ $(docker ps -a -q --filter ancestor=$img --format="{{.ID}}"|wc -l) -ge 1 ]; then
						docker rm $(docker stop $(docker ps -a -q --filter ancestor=pingouinfinihub/menhir --format="{{.ID}}"))
					fi
					
					docker rmi $img
				done
				
				echo ""
				echo $(date +%H:%M:%S)   "INFO   Pull des images depuis dockerhub"
				for img in $dockerImages; do
					:
					docker pull $img
				done
				
				echo ""
				echo $(date +%H:%M:%S)   "INFO   Sauvegarde des images au format tar dans le répertoire $(pwd)/images"
				for img in $dockerImages; do
					# Set comma as delimiter & Read the split words into an array based on comma delimiter
					delimiter="/"
					string=$img$delimiter
					
					myarray=()
					while [[ $string ]]; do
					  myarray+=( "${string%%"$delimiter"*}" )
					  string=${string#*"$delimiter"}
					done
					echo $(date +%H:%M:%S)   "DEBUG  Creation de $WORKING_DIR/images/${myarray[1]}.tar"
					
					docker save -o  $WORKING_DIR/images/${myarray[1]}.tar $img
				done
				
				echo ""
				echo $(date +%H:%M:%S)   "INFO   Preparation des $NBCONTAINERS container(s)"
				
				
				 # Preparer les yml pour lancer $NBCONTAINERS
				 # TODO
				
				
				
				cd $CURRENT_PATH
				
				echo ""
				echo $(date +%H:%M:%S)   "INFO    Termine"
				exit
				;;&
			 

			 *02*)
				  DOCKER_RELEASE=1.28.2
				  echo "### Installation de Docker ..."
				  install_docker $DOCKER_RELEASE
				  echo "Terminé"
				  echo "***************************************"
				  echo $(docker --version)
				  echo "***************************************"
				  echo ""
				  ;;&
	esac
	

  PORTAPP=$(whiptail --inputbox "Sur quel port souhaitez-vous servir l'application ?" 8 39 18081 --title "App port" 3>&1 1>&2 2>&3)
                                                                        # A trick to swap stdout and stderr.
  # Again, you can pack this inside if, but it seems really long for some 80-col terminal users.
  exitstatus=$?
  if [ ! $exitstatus = 0 ]; then
    exit
  fi
  
  #echo "User selected Ok and entered " $PORTAPP
  
  (whiptail --title "Persistance" --yesno "Souhaitez-vous que les données de la base de données soient persistées? (conservées même si le container est éteint)" 8 78)
  #PERSISTENCE = 0 = true
  PERSISTENCE=$?.
  echo "User selected Ok and entered " $PERSISTENCE
  
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