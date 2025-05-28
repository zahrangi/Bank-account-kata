package net.zz.ebankbackend.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.zz.ebankbackend.dtos.*;
import net.zz.ebankbackend.entities.*;
import net.zz.ebankbackend.enums.OperationType;
import net.zz.ebankbackend.exceptions.BalanceNotSufficientException;
import net.zz.ebankbackend.exceptions.BankAccountNotFoundException;
import net.zz.ebankbackend.exceptions.BankClientNotFoundException;
import net.zz.ebankbackend.mappers.BankAccountMapperImpl;
import net.zz.ebankbackend.repositories.AccountOperationRepository;
import net.zz.ebankbackend.repositories.BankAccountRepository;
import net.zz.ebankbackend.repositories.BankClientRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {
    private BankClientRepository bankClientRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationRepository accountOperationRepository;
    private BankAccountMapperImpl dtoMapper;

    @Override
    public BankClientDTO saveBankClient(BankClientDTO BankClientDTO) {
        log.info("Saving new Bank Client");
        BankClient bankClient=dtoMapper.fromBankClientDTO(BankClientDTO);
        BankClient savedbankClient = bankClientRepository.save(bankClient);
        return dtoMapper.frombankClient(savedbankClient);
    }

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long bankClientId) throws BankClientNotFoundException {
        BankClient bankClient=bankClientRepository.findById(bankClientId).orElse(null);
        if(bankClient==null)
            throw new BankClientNotFoundException("bankClient not found");
        CurrentAccount currentAccount=new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreatedAt(new Date());
        currentAccount.setBalance(initialBalance);
        currentAccount.setOverDraft(overDraft);
        currentAccount.setBankClient(bankClient);
        CurrentAccount savedBankAccount = bankAccountRepository.save(currentAccount);
        return dtoMapper.fromCurrentBankAccount(savedBankAccount);
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long bankClientId) throws BankClientNotFoundException {
        BankClient bankClient=bankClientRepository.findById(bankClientId).orElse(null);
        if(bankClient==null)
            throw new BankClientNotFoundException("bankClient not found");
        SavingAccount savingAccount=new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreatedAt(new Date());
        savingAccount.setBalance(initialBalance);
        savingAccount.setInterestRate(interestRate);
        savingAccount.setBankClient(bankClient);
        SavingAccount savedBankAccount = bankAccountRepository.save(savingAccount);
        return dtoMapper.fromSavingBankAccount(savedBankAccount);
    }

    @Override
    public List<BankClientDTO> listBankClients() {
        List<BankClient> bankClients = bankClientRepository.findAll();
        List<BankClientDTO> BankClientDTOS = bankClients.stream()
                .map(bankClient -> dtoMapper.frombankClient(bankClient))
                .collect(Collectors.toList());
        /*
        List<BankClientDTO> BankClientDTOS=new ArrayList<>();
        for (bankClient bankClient:bankClients){
            BankClientDTO BankClientDTO=dtoMapper.frombankClient(bankClient);
            BankClientDTOS.add(BankClientDTO);
        }
        *
         */
        return BankClientDTOS;
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId)
                .orElseThrow(()->new BankAccountNotFoundException("BankAccount not found"));
        if(bankAccount instanceof SavingAccount){
            SavingAccount savingAccount= (SavingAccount) bankAccount;
            return dtoMapper.fromSavingBankAccount(savingAccount);
        } else {
            CurrentAccount currentAccount= (CurrentAccount) bankAccount;
            return dtoMapper.fromCurrentBankAccount(currentAccount);
        }
    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId)
                .orElseThrow(()->new BankAccountNotFoundException("BankAccount not found"));
        if(bankAccount.getBalance()<amount)
            throw new BalanceNotSufficientException("Balance not sufficient");
        AccountOperation accountOperation=new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()-amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId)
                .orElseThrow(()->new BankAccountNotFoundException("BankAccount not found"));
        AccountOperation accountOperation=new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()+amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException {
        debit(accountIdSource,amount,"Transfer to "+accountIdDestination);
        credit(accountIdDestination,amount,"Transfer from "+accountIdSource);
    }
    @Override
    public List<BankAccountDTO> bankAccountList(){
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount) {
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return dtoMapper.fromSavingBankAccount(savingAccount);
            } else {
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return dtoMapper.fromCurrentBankAccount(currentAccount);
            }
        }).collect(Collectors.toList());
        return bankAccountDTOS;
    }
    @Override
    public BankClientDTO getBankClient(Long bankClientId) throws BankClientNotFoundException {
        BankClient bankClient = bankClientRepository.findById(bankClientId)
                .orElseThrow(() -> new BankClientNotFoundException("bankClient Not found"));
        return dtoMapper.frombankClient(bankClient);
    }

    @Override
    public BankClientDTO updateBankClient(BankClientDTO bankClientDTO) {
        log.info("Saving new bankClient");
        BankClient bankClient=dtoMapper.fromBankClientDTO(bankClientDTO);
        BankClient savedbankClient = bankClientRepository.save(bankClient);
        return dtoMapper.frombankClient(savedbankClient);
    }
    @Override
    public void deleteBankClient(Long bankClientId){
        bankClientRepository.deleteById(bankClientId);
    }
    @Override
    public List<AccountOperationDTO> accountHistory(String accountId){
        List<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountId);
        return accountOperations.stream().map(op->dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        return null;
    }

    @Override
    public List<BankClientDTO> searchBankClients(String keyword) {
        List<BankClient> bankClients=bankClientRepository.searchbankClient(keyword);
        List<BankClientDTO> BankClientDTOS = bankClients.stream().map(cust -> dtoMapper.frombankClient(cust)).collect(Collectors.toList());
        return BankClientDTOS;
    }
}
