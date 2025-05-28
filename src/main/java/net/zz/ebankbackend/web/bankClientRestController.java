package net.zz.ebankbackend.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.zz.ebankbackend.dtos.BankClientDTO;
import net.zz.ebankbackend.exceptions.BankClientNotFoundException;
import net.zz.ebankbackend.services.BankAccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@CrossOrigin("*")
public class bankClientRestController {
    private BankAccountService bankAccountService;

    @GetMapping("/bankClients")
    public List<BankClientDTO> bankCLients() {
        return bankAccountService.listBankClients();
    }

    @GetMapping("/bankClients/search")
    public List<BankClientDTO> searchBankClientss(@RequestParam(name = "keyword", defaultValue = "") String keyword) {
        return bankAccountService.searchBankClients("%" + keyword + "%");
    }

    @GetMapping("/bankClients/{id}")
    public BankClientDTO getBankClient(@PathVariable(name = "id") Long bankCLientId) throws BankClientNotFoundException {
        return bankAccountService.getBankClient(bankCLientId);
    }

    @PostMapping("/bankClients")
    public BankClientDTO saveBankClient(@RequestBody BankClientDTO BankClientDTO) {
        return bankAccountService.saveBankClient(BankClientDTO);
    }

    @PutMapping("/bankClients/{bankClientId}")
    public BankClientDTO updateBankClient(@PathVariable Long bankClientId, @RequestBody BankClientDTO BankClientDTO) {
        BankClientDTO.setId(bankClientId);
        return bankAccountService.updateBankClient(BankClientDTO);
    }

    @DeleteMapping("/bankClients/{id}")
    public void deleteBankClient(@PathVariable Long id) {
        bankAccountService.deleteBankClient(id);
    }
}
