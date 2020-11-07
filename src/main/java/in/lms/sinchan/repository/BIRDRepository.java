package in.lms.sinchan.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import in.lms.sinchan.entity.BIRD;

@Repository
public interface BIRDRepository extends MongoRepository<BIRD, String> {

    @Query(value = "{'isActive':true}")
    public List<BIRD> findBooksByIssuedExpiryDate(Date date);

    @Query(value = "{'isActive':true}")
    public List<BIRD> findAllBIRD();

    @Query(value = "{'isActive':true}")
    public BIRD findBIRDByBookId(String id);

}
