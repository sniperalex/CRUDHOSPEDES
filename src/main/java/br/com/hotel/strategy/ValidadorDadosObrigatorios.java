package br.com.hotel.strategy;

import br.com.hotel.model.Hospede;

public class ValidadorDadosObrigatorios implements IValidador {

    @Override
    public String validar(Hospede hospede) {
        StringBuilder erros = new StringBuilder();

        // Dados Pessoais
        if (hospede.getNome() == null || hospede.getNome().trim().isEmpty()) {
            erros.append("Nome completo é obrigatório. ");
        }
        if (hospede.getDataNascimento() == null) {
            erros.append("Data de Nascimento é obrigatória. ");
        }
        if (hospede.getEmail() == null || hospede.getEmail().trim().isEmpty()) {
            erros.append("E-mail é obrigatório. ");
        }
        if (hospede.getTelefone() == null || hospede.getTelefone().trim().isEmpty()) {
            erros.append("Telefone é obrigatório. ");
        }

        // Endereço Completo (Exigido pela RN0201)
        if (hospede.getCep() == null || hospede.getCep().trim().isEmpty()) {
            erros.append("CEP é obrigatório. ");
        }
        if (hospede.getLogradouro() == null || hospede.getLogradouro().trim().isEmpty()) {
            erros.append("Logradouro é obrigatório. ");
        }
        if (hospede.getNumero() == null || hospede.getNumero().trim().isEmpty()) {
            erros.append("Número é obrigatório. ");
        }
        if (hospede.getBairro() == null || hospede.getBairro().trim().isEmpty()) {
            erros.append("Bairro é obrigatório. ");
        }
        if (hospede.getCidade() == null || hospede.getCidade().trim().isEmpty()) {
            erros.append("Cidade é obrigatória. ");
        }
        if (hospede.getEstado() == null || hospede.getEstado().trim().isEmpty()) {
            erros.append("Estado é obrigatório. ");
        }

        if (erros.length() > 0) {
            return erros.toString();
        }
        
        return null; // Sem erros
    }
}