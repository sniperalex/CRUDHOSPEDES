package br.com.hotel.strategy;
import br.com.hotel.model.Hospede;

public interface IValidador {
    String validar(Hospede hospede);
}