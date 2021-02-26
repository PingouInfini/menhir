package com.capgemini.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.capgemini.web.rest.TestUtil;

public class IndividuTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Individu.class);
        Individu individu1 = new Individu();
        individu1.setId(1L);
        Individu individu2 = new Individu();
        individu2.setId(individu1.getId());
        assertThat(individu1).isEqualTo(individu2);
        individu2.setId(2L);
        assertThat(individu1).isNotEqualTo(individu2);
        individu1.setId(null);
        assertThat(individu1).isNotEqualTo(individu2);
    }
}
