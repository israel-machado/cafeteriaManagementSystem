package repositories;

import model.Lote.LoteDomain;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LoteRepository extends MongoRepository<LoteDomain, String> {
}
