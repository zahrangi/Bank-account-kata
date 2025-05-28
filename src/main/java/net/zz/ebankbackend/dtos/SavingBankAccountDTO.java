package net.zz.ebankbackend.dtos;

import lombok.Data;
import net.zz.ebankbackend.entities.BankClient;
import net.zz.ebankbackend.enums.AccountStatus;

import java.util.Date;

@Data
public class SavingBankAccountDTO extends BankAccountDTO {
    private String id;
    private double balance;
    private Date createdAt;
    private AccountStatus status;
    private BankClientDTO bankClientDTO;
    private double interestRate;
}
