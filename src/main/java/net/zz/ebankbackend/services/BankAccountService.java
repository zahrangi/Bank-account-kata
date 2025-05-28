package net.zz.ebankbackend.services;


import net.zz.ebankbackend.dtos.*;
import net.zz.ebankbackend.exceptions.BalanceNotSufficientException;
import net.zz.ebankbackend.exceptions.BankAccountNotFoundException;
import net.zz.ebankbackend.exceptions.BankClientNotFoundException;

import java.util.List;

public interface BankAccountService {

    BankClientDTO saveBankClient(BankClientDTO BankClientDTO);
    CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long bankClientId) throws BankClientNotFoundException;
    SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long bankClientId) throws BankClientNotFoundException;
    List<BankClientDTO> listBankClients();
    BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException;
    void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException;
    void credit(String accountId, double amount, String description) throws BankAccountNotFoundException;
    void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException;

    List<BankAccountDTO> bankAccountList();

    BankClientDTO getBankClient(Long bankClientId) throws BankClientNotFoundException;

    BankClientDTO updateBankClient(BankClientDTO BankClientDTO);

    void deleteBankClient(Long bankClientId);

    List<AccountOperationDTO> accountHistory(String accountId);

    AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;

    List<BankClientDTO> searchBankClients(String keyword);
}
