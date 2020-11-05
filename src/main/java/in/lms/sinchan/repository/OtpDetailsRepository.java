package in.lms.sinchan.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import in.lms.sinchan.entity.OtpDetails;

@Repository
public interface OtpDetailsRepository extends MongoRepository<OtpDetails, String> {

    public OtpDetails findOtpDetailsById(String studentId);

}
