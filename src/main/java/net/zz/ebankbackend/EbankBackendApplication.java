package net.zz.ebankbackend;

import net.zz.ebankbackend.dtos.BankAccountDTO;
import net.zz.ebankbackend.dtos.BankClientDTO;
import net.zz.ebankbackend.dtos.CurrentBankAccountDTO;
import net.zz.ebankbackend.dtos.SavingBankAccountDTO;
import net.zz.ebankbackend.exceptions.BankClientNotFoundException;
import net.zz.ebankbackend.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankBackendApplication.class, args);
    }


    @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService){
        return args -> {
            Stream.of("Client1","Client2","Client3").forEach(name->{
                BankClientDTO bankClient=new BankClientDTO();
                bankClient.setName(name);
                bankClient.setEmail(name+"@gmail.com");
                bankAccountService.saveBankClient(bankClient);
            });
            bankAccountService.listBankClients().forEach(bankClient->{
                try {
                    bankAccountService.saveCurrentBankAccount(Math.random()*111111,33333,bankClient.getId());
                    bankAccountService.saveSavingBankAccount(Math.random()*444444,5.5,bankClient.getId());

                } catch (BankClientNotFoundException e) {
                    e.printStackTrace();
                }
            });
            List<BankAccountDTO> bankAccounts = bankAccountService.bankAccountList();
            for (BankAccountDTO bankAccount:bankAccounts){
                for (int i = 0; i <10 ; i++) {
                    String accountId;
                    if(bankAccount instanceof SavingBankAccountDTO){
                        accountId=((SavingBankAccountDTO) bankAccount).getId();
                    } else{
                        accountId=((CurrentBankAccountDTO) bankAccount).getId();
                    }
                    bankAccountService.credit(accountId,10000+Math.random()*22222,"Credit");
                    bankAccountService.debit(accountId,1000+Math.random()*11111,"Debit");
                }
            }
        };
    }

}
