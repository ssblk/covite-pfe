package com.pfe.covite.repository.search;

import com.pfe.covite.domain.Position;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Position} entity.
 */
public interface PositionSearchRepository extends ElasticsearchRepository<Position, Long> {
}
