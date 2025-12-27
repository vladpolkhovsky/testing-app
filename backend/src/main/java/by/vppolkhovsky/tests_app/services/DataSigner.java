package by.vppolkhovsky.tests_app.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class DataSigner {

    private static String algorithm = "SHA256withRSA";

    private KeyPair keyPair;

    @PostConstruct
    @SneakyThrows
    public void init() {
        // Генерируем ключи автоматически
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        keyPair = keyGen.generateKeyPair();

        // Тестируем
        String testData = "test";
        String signature = createSignature(testData);
        boolean valid = verifySignature(testData, signature);

        if (!valid) {
            throw new IllegalStateException("Self-test failed");
        }
    }

    @SneakyThrows
    public String createSignature(String data) {
        Signature signature = Signature.getInstance(algorithm);
        signature.initSign(keyPair.getPrivate());
        signature.update(data.getBytes());
        byte[] digitalSignature = signature.sign();
        return Base64.getEncoder().encodeToString(digitalSignature);
    }

    @SneakyThrows
    public boolean verifySignature(String data, String signatureStr) {
        Signature signature = Signature.getInstance(algorithm);
        signature.initVerify(keyPair.getPublic());
        signature.update(data.getBytes());
        byte[] signatureBytes = Base64.getDecoder().decode(signatureStr);
        return signature.verify(signatureBytes);
    }

    public String getPublicKeyBase64() {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    public String getPrivateKeyBase64() {
        return Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    }
}