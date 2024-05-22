package tech.buildrun.picpay.service;

import org.springframework.stereotype.Service;

import tech.buildrun.picpay.controller.dto.CreateWalletDTO;
import tech.buildrun.picpay.entity.Wallet;
import tech.buildrun.picpay.exception.WalletDataAlreadyExistsException;
import tech.buildrun.picpay.repository.WalletRepository;

@Service
public class WalletService {
    
    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }


    public Wallet createWallet(CreateWalletDTO dto) {

        var walletDb = walletRepository.findByCpfCnpjOrEmail(dto.cpfCnpj(), dto.email());
        if (walletDb.isPresent()) {
            throw new WalletDataAlreadyExistsException("CpfCnpj or Email already exists");
        }
        return walletRepository.save(dto.toWallet());
    }
}
