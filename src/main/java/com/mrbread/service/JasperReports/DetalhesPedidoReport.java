package com.mrbread.service.JasperReports;

import com.mrbread.config.exception.AppException;
import com.mrbread.config.security.SecurityUtils;
import com.mrbread.domain.model.Organizacao;
import com.mrbread.domain.repository.OrganizacaoRepository;
import com.mrbread.dto.PedidoDTO;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DetalhesPedidoReport {

    private final OrganizacaoRepository organizacaoRepository;

    @Value("${reports.path:/app/reports}")
    private String reportsPath;               // pasta dentro do container (mapeada para ./reports na máquina host)

    /**
     * Gera o PDF do pedido, grava no volume compartilhado
     * e devolve o caminho absoluto do arquivo dentro do container.
     */
    public String generateReport(PedidoDTO pedidoDto) {

        // 1) Recupera a organização logada
        Organizacao organizacao = organizacaoRepository
                .findByIdOrg(SecurityUtils.obterOrganizacaoId())
                .orElseThrow(() -> new AppException(
                        "Organização não encontrado",
                        "A organização é inválida",
                        HttpStatus.NOT_FOUND));

        try (InputStream jasperStream =
                     getClass().getResourceAsStream("/jasper/orderDetails/DetalhePedido.jasper")) {

            // 2) Carrega o relatório já compilado (.jasper)
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

            // 3) Monta os parâmetros
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("cliente",      pedidoDto.getNomeFantasiaCliente());
            parameters.put("pedidoId",     pedidoDto.getIdPedido());
            parameters.put("dataCriacao",  pedidoDto.getDataCriacao());
            parameters.put("total",        pedidoDto.getPrecoTotal());
            parameters.put("organizacao",  organizacao.getNomeOrganizacao());

            // DataSource para os itens do pedido
            JRBeanCollectionDataSource itensDataSource =
                    new JRBeanCollectionDataSource(pedidoDto.getItens());
            parameters.put("itensDataSource", itensDataSource);

            // 4) Preenche o relatório
            JasperPrint jasperPrint =
                    JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

            // 5) Garante que a pasta de relatórios exista
            Path reportsDir = Paths.get(reportsPath);
            Files.createDirectories(reportsDir);

            // 6) Define nome do arquivo e caminho completo
            String fileName = generateFileName(pedidoDto.getIdPedido().toString());
            Path   filePath = reportsDir.resolve(fileName);

            // 7) Exporta para PDF
            JasperExportManager.exportReportToPdfFile(jasperPrint, filePath.toString());

            // Retorna caminho (dentro do container); na máquina host estará em ./reports
            return filePath.toString();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatório do pedido", e);
        }
    }

    /* Gera um nome de arquivo único: pedido_<id>_yyyyMMdd_HHmmss.pdf */
    private String generateFileName(String pedidoId) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("pedido_%s_%s.pdf", pedidoId, timestamp);
    }
}