package br.com.hotel.strategy;
import br.com.hotel.model.Hospede;

public class ValidadorCpf implements IValidador {
    @Override
    public String validar(Hospede hospede) {
        String cpf = hospede.getCpf();
        if (cpf == null || cpf.trim().isEmpty()) {
            return "CPF é obrigatório.";
        }
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        if (cpfLimpo.length() != 11) {
            return "CPF deve conter 11 dígitos.";
        }
        return null; 
    }
}