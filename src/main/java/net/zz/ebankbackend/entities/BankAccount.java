package net.zz.ebankbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.zz.ebankbackend.enums.AccountStatus;

import java.util.Date;
import java.util.List;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class BankAccount {
    @Id
    private String id;
    private String name;
    private double balance;
    private Date createdAt;
    private Date updatedAt;
    private AccountStatus status;
    @ManyToOne
    private BankClient bankClient;
    @OneToMany(mappedBy = "bankAccount")
    private List<AccountOperation> accountOperations;
}
