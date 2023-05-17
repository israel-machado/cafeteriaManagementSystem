package repositories;

import model.Material.MaterialDomain;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MaterialRepository extends MongoRepository<MaterialDomain, String> {
}
