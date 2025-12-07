package br.com.hotel.strategy;

import br.com.hotel.model.Hospede;

public class ValidadorCpf implements IValidador {

    @Override
    public String validar(Hospede hospede) {
        String cpf = hospede.getCpf();
        
        if (cpf == null || cpf.trim().isEmpty()) {
            return "CPF é obrigatório.";
        }

        // Remove tudo que não é número
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");

        // Verifica tamanho
        if (cpfLimpo.length() != 11) {
            return "CPF deve conter 11 dígitos.";
        }

        // Verifica padrões conhecidos de CPF inválido (ex: 111.111.111-11)
        if (cpfLimpo.matches("(\\d)\\1{10}")) {
            return "CPF inválido (números repetidos).";
        }

        // --- CÁLCULO DOS DÍGITOS VERIFICADORES ---
        try {
            // 1º Dígito
            int soma = 0;
            int peso = 10;
            for (int i = 0; i < 9; i++) {
                int num = (int) (cpfLimpo.charAt(i) - 48);
                soma = soma + (num * peso);
                peso = peso - 1;
            }

            int r = 11 - (soma % 11);
            char dig10 = (r == 10 || r == 11) ? '0' : (char) (r + 48);

            // 2º Dígito
            soma = 0;
            peso = 11;
            for (int i = 0; i < 10; i++) {
                int num = (int) (cpfLimpo.charAt(i) - 48);
                soma = soma + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (soma % 11);
            char dig11 = (r == 10 || r == 11) ? '0' : (char) (r + 48);

            // Validação final
            if (dig10 != cpfLimpo.charAt(9) || dig11 != cpfLimpo.charAt(10)) {
                return "CPF inválido (Dígitos verificadores não conferem).";
            }

        } catch (Exception e) {
            return "Erro ao validar CPF.";
        }
        
        return null; // CPF Válido
    }
}