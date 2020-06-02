package com.pfe.covite.repository.search;

import com.pfe.covite.domain.Commande;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Commande} entity.
 */
public interface CommandeSearchRepository extends ElasticsearchRepository<Commande, Long> {
}
