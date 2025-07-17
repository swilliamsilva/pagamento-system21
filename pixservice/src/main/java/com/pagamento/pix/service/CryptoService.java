package com.pagamento.pix.service;

public interface CryptoService {
    String encrypt(String plainText);
    String decrypt(String encryptedText);
}