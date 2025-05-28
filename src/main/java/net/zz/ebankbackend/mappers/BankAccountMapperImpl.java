package net.zz.ebankbackend.mappers;

import net.zz.ebankbackend.dtos.AccountOperationDTO;
import net.zz.ebankbackend.dtos.BankClientDTO;
import net.zz.ebankbackend.dtos.CurrentBankAccountDTO;
import net.zz.ebankbackend.dtos.SavingBankAccountDTO;
import net.zz.ebankbackend.entities.AccountOperation;
import net.zz.ebankbackend.entities.BankClient;
import net.zz.ebankbackend.entities.CurrentAccount;
import net.zz.ebankbackend.entities.SavingAccount;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class BankAccountMapperImpl {
    public BankClientDTO frombankClient(BankClient bankClient){
        BankClientDTO bankClientDTO=new BankClientDTO();
        BeanUtils.copyProperties(bankClient,bankClientDTO);
        return  bankClientDTO;
    }
    public BankClient fromBankClientDTO(BankClientDTO bankClientDTO){
        BankClient bankClient=new BankClient();
        BeanUtils.copyProperties(bankClientDTO,bankClient);
        return  bankClient;
    }

    public SavingBankAccountDTO fromSavingBankAccount(SavingAccount savingAccount){
        SavingBankAccountDTO savingBankAccountDTO=new SavingBankAccountDTO();
        BeanUtils.copyProperties(savingAccount,savingBankAccountDTO);
        savingBankAccountDTO.setBankClientDTO(frombankClient(savingAccount.getBankClient()));
        savingBankAccountDTO.setType(savingAccount.getClass().getSimpleName());
        return savingBankAccountDTO;
    }

    public SavingAccount fromSavingBankAccountDTO(SavingBankAccountDTO savingBankAccountDTO){
        SavingAccount savingAccount=new SavingAccount();
        BeanUtils.copyProperties(savingBankAccountDTO,savingAccount);
        savingAccount.setBankClient(fromBankClientDTO(savingBankAccountDTO.getBankClientDTO()));
        return savingAccount;
    }

    public CurrentBankAccountDTO fromCurrentBankAccount(CurrentAccount currentAccount){
        CurrentBankAccountDTO currentBankAccountDTO=new CurrentBankAccountDTO();
        BeanUtils.copyProperties(currentAccount,currentBankAccountDTO);
        currentBankAccountDTO.setBankClientDTO(frombankClient(currentAccount.getBankClient()));
        //currentBankAccountDTO.setType(currentAccount.getClass().getSimpleName());
        return currentBankAccountDTO;
    }

    public CurrentAccount fromCurrentBankAccountDTO(CurrentBankAccountDTO currentBankAccountDTO){
        CurrentAccount currentAccount=new CurrentAccount();
        BeanUtils.copyProperties(currentBankAccountDTO,currentAccount);
        currentAccount.setBankClient(fromBankClientDTO(currentBankAccountDTO.getBankClientDTO()));
        return currentAccount;
    }

    public AccountOperationDTO fromAccountOperation(AccountOperation accountOperation){
        AccountOperationDTO accountOperationDTO=new AccountOperationDTO();
        BeanUtils.copyProperties(accountOperation,accountOperationDTO);
        return accountOperationDTO;
    }

}
