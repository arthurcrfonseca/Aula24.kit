package br.com.starlog.controller;

import br.com.starlog.model.Carga;
import br.com.starlog.view.TelaCadastroCarga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * CAMADA CONTROLLER (O Maestro da Aplicação)
 * Responsável pela conversão dos dados, orquestração do cadastro,
 * tratamento de erros e manutenção do repositório em memória.
 */
public class CargaController {

    private final List<Carga> repositorio = new ArrayList<>();

    public void cadastrarCarga(String codigoRaw, String categoriaRaw,
                               String pesoRaw, String valorRaw, TelaCadastroCarga tela) {
        try {
            // PASSO 1: Validar campos numéricos e converter vírgula decimal para ponto.
            if (pesoRaw == null || pesoRaw.trim().isEmpty()
                    || valorRaw == null || valorRaw.trim().isEmpty()) {
                throw new NumberFormatException();
            }

            double peso = Double.parseDouble(pesoRaw.trim().replace(",", "."));
            double valor = Double.parseDouble(valorRaw.trim().replace(",", "."));

            // PASSO 2: Criar a entidade. O Model aplica as regras de negócio.
            Carga novaCarga = new Carga(codigoRaw, categoriaRaw, peso, valor);

            // PASSO 3: Armazenar a carga somente depois de validada.
            this.repositorio.add(novaCarga);

            // PASSO 4: Informar sucesso e atualizar o relatório.
            tela.exibirMensagemSucesso("Carga cadastrada com sucesso!\n" + novaCarga.toString());
            tela.atualizarRelatorio(gerarTextoRelatorio());

        } catch (NumberFormatException e) {
            tela.exibirMensagemErro(
                    "Erro de formato numérico: Digite apenas números válidos para Peso e Valor!"
            );
        } catch (IllegalArgumentException e) {
            tela.exibirMensagemErro("Regra de Negócio Violada: " + e.getMessage());
        }
    }

    /**
     * Retorna a lista de cargas como texto formatado para exibição na JTextArea da View.
     */
    public String gerarTextoRelatorio() {
        if (repositorio.isEmpty()) {
            return "Nenhuma carga registrada na esteira.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("--- RELATÓRIO OFICIAL DE DESPACHO ---\n");
        double totalSeguro = 0.0;
        double pesoTotal = 0.0;

        for (int i = 0; i < repositorio.size(); i++) {
            Carga c = repositorio.get(i);
            sb.append(String.format("%02d. %s\n", (i + 1), c.toString()));
            totalSeguro += c.calcularSeguro();
            pesoTotal += c.getPeso();
        }

        sb.append("-------------------------------------\n");
        sb.append(String.format("Total de Cargas: %d | Peso Total: %.2f kg | Seguro Acumulado: R$ %.2f\n",
                repositorio.size(), pesoTotal, totalSeguro));

        return sb.toString();
    }

    public List<Carga> getRepositorio() {
        return Collections.unmodifiableList(repositorio);
    }
}
