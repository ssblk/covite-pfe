package com.pfe.covite.repository.search;

import com.pfe.covite.domain.Voiture;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Voiture} entity.
 */
public interface VoitureSearchRepository extends ElasticsearchRepository<Voiture, Long> {
}
