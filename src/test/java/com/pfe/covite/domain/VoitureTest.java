package com.pfe.covite.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.pfe.covite.web.rest.TestUtil;

public class VoitureTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Voiture.class);
        Voiture voiture1 = new Voiture();
        voiture1.setId(1L);
        Voiture voiture2 = new Voiture();
        voiture2.setId(voiture1.getId());
        assertThat(voiture1).isEqualTo(voiture2);
        voiture2.setId(2L);
        assertThat(voiture1).isNotEqualTo(voiture2);
        voiture1.setId(null);
        assertThat(voiture1).isNotEqualTo(voiture2);
    }
}
