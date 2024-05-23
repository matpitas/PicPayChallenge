package tech.buildrun.picpay.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tech.buildrun.picpay.controller.dto.TransferDTO;
import tech.buildrun.picpay.entity.Transfer;
import tech.buildrun.picpay.entity.Wallet;
import tech.buildrun.picpay.exception.InsufficientBalanceException;
import tech.buildrun.picpay.exception.TransferNotAllowedForWalletTypeException;
import tech.buildrun.picpay.exception.TransferNotAuthorizedException;
import tech.buildrun.picpay.exception.WalletNotFoundException;
import tech.buildrun.picpay.repository.TransferRepository;
import tech.buildrun.picpay.repository.WalletRepository;

@Service
public class TransferService {

    private final TransferRepository transferRepository;

    private final AuthorizationService authorizationService;
    private final NotificationService notificationService;

    private final WalletRepository walletRepository;


    
    public TransferService(TransferRepository transferRepository, AuthorizationService authorizationService,
            NotificationService notificationService, WalletRepository walletRepository) {
        this.transferRepository = transferRepository;
        this.authorizationService = authorizationService;
        this.notificationService = notificationService;
        this.walletRepository = walletRepository;
    }

    @Transactional
    public Transfer transfer(TransferDTO transferDTO) { 
        var sender = walletRepository.findById(transferDTO.payer()).orElseThrow(() -> new WalletNotFoundException(transferDTO.payer()));

        var receiver = walletRepository.findById(transferDTO.payee()).orElseThrow(() -> new WalletNotFoundException(transferDTO.payee()));

        validateTransfer(transferDTO, sender);

        sender.debit(transferDTO.value());
        receiver.credit(transferDTO.value());

        var transfer = new Transfer(sender, receiver, transferDTO.value());

        walletRepository.save(sender);
        walletRepository.save(receiver);
        var transferResult = transferRepository.save(transfer);

        CompletableFuture.runAsync(() -> notificationService.sendNotification(transferResult));

        return transferResult;
    } 


    private void validateTransfer(TransferDTO transferDTO, Wallet sender) {

        if(!sender.isTransferAllowedForWalletType()) {
            throw new TransferNotAllowedForWalletTypeException();
        }

        if(!sender.isBalanceEqualOrGreaterThan(transferDTO.value())){
            throw new InsufficientBalanceException();
        }

        if(!authorizationService.isAuthorized(transferDTO)) {
            throw new TransferNotAuthorizedException();
        }


    }
}
