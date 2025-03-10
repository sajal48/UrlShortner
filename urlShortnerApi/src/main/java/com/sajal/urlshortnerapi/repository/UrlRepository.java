package com.sajal.urlshortnerapi.repository;

import com.sajal.urlshortnerapi.entity.UrlMapping;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends CassandraRepository<UrlMapping, String> {
}
