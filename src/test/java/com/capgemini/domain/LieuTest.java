package com.capgemini.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.capgemini.web.rest.TestUtil;

public class LieuTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Lieu.class);
        Lieu lieu1 = new Lieu();
        lieu1.setId(1L);
        Lieu lieu2 = new Lieu();
        lieu2.setId(lieu1.getId());
        assertThat(lieu1).isEqualTo(lieu2);
        lieu2.setId(2L);
        assertThat(lieu1).isNotEqualTo(lieu2);
        lieu1.setId(null);
        assertThat(lieu1).isNotEqualTo(lieu2);
    }
}
