package net.zz.ebankbackend.repositories;

import net.zz.ebankbackend.entities.BankClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BankClientRepository extends JpaRepository<BankClient,Long> {
    @Query("select bc from BankClient bc where bc.name like :kw")
    List<BankClient> searchbankClient(@Param("kw") String keyword);
}
