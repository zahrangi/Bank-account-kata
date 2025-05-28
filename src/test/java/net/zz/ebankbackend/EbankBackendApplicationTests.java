package net.zz.ebankbackend;

import net.zz.ebankbackend.dtos.*;
import net.zz.ebankbackend.entities.*;
import net.zz.ebankbackend.exceptions.*;
import net.zz.ebankbackend.mappers.BankAccountMapperImpl;
import net.zz.ebankbackend.repositories.*;
import net.zz.ebankbackend.services.BankAccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class EbankBackendApplicationTests {

    @Mock
    private BankClientRepository bankClientRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private AccountOperationRepository accountOperationRepository;

    @Mock
    private BankAccountMapperImpl dtoMapper;

    @InjectMocks
    private BankAccountServiceImpl bankAccountService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveBankClient() {
        BankClientDTO dto = new BankClientDTO();
        BankClient entity = new BankClient();

        when(dtoMapper.fromBankClientDTO(dto)).thenReturn(entity);
        when(bankClientRepository.save(entity)).thenReturn(entity);
        when(dtoMapper.frombankClient(entity)).thenReturn(dto);

        BankClientDTO result = bankAccountService.saveBankClient(dto);
        assertNotNull(result);
        verify(bankClientRepository).save(entity);
    }

    @Test
    public void testSaveCurrentBankAccount_Success() throws BankClientNotFoundException {
        Long clientId = 1L;
        BankClient client = new BankClient();
        CurrentAccount account = new CurrentAccount();
        CurrentBankAccountDTO dto = new CurrentBankAccountDTO();

        when(bankClientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(bankAccountRepository.save(any(CurrentAccount.class))).thenReturn(account);
        when(dtoMapper.fromCurrentBankAccount(account)).thenReturn(dto);

        CurrentBankAccountDTO result = bankAccountService.saveCurrentBankAccount(1000.0, 500.0, clientId);
        assertNotNull(result);
    }

    @Test
    public void testSaveCurrentBankAccount_BankClientNotFound() {
        when(bankClientRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(BankClientNotFoundException.class, () -> {
            bankAccountService.saveCurrentBankAccount(1000.0, 500.0, 99L);
        });
    }

    @Test
    public void testDebit_Success() throws BankAccountNotFoundException, BalanceNotSufficientException {
        String accId = "acc123";
        BankAccount account = new CurrentAccount();
        account.setBalance(1000.0);

        when(bankAccountRepository.findById(accId)).thenReturn(Optional.of(account));

        bankAccountService.debit(accId, 200.0, "Test debit");

        assertEquals(800.0, account.getBalance());
        verify(accountOperationRepository).save(any(AccountOperation.class));
    }

    @Test
    public void testDebit_InsufficientBalance() {
        String accId = "acc123";
        BankAccount account = new SavingAccount();
        account.setBalance(100.0);

        when(bankAccountRepository.findById(accId)).thenReturn(Optional.of(account));

        assertThrows(BalanceNotSufficientException.class, () -> {
            bankAccountService.debit(accId, 200.0, "Test debit");
        });
    }

    @Test
    public void testTransfer_Success() throws Exception {
        String sourceId = "src";
        String destId = "dest";

        BankAccount source = new CurrentAccount();
        source.setBalance(1000.0);

        BankAccount dest = new SavingAccount();
        dest.setBalance(500.0);

        when(bankAccountRepository.findById(sourceId)).thenReturn(Optional.of(source));
        when(bankAccountRepository.findById(destId)).thenReturn(Optional.of(dest));

        bankAccountService.transfer(sourceId, destId, 200.0);

        assertEquals(800.0, source.getBalance());
        assertEquals(700.0, dest.getBalance());
    }
}

