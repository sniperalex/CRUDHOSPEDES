package br.com.hotel.strategy;
import br.com.hotel.model.Hospede;

public class ValidadorDadosObrigatorios implements IValidador {
    @Override
    public String validar(Hospede hospede) {
        StringBuilder erros = new StringBuilder();
        if (hospede.getNome() == null || hospede.getNome().isEmpty()) erros.append("Nome é obrigatório. ");
        if (hospede.getDataNascimento() == null) erros.append("Data de Nascimento é obrigatória. ");
        if (hospede.getEmail() == null || !hospede.getEmail().contains("@")) erros.append("E-mail inválido. ");
        if (hospede.getCidade() == null || hospede.getCidade().isEmpty()) erros.append("Cidade é obrigatória. ");

        return erros.length() > 0 ? erros.toString() : null;
    }
}