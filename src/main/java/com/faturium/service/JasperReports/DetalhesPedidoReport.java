package com.faturium.service.JasperReports;

import com.faturium.config.exception.AppException;
import com.faturium.config.security.SecurityUtils;
import com.faturium.domain.model.Organizacao;
import com.faturium.domain.repository.OrganizacaoRepository;
import com.faturium.dto.DetalhesPedidoDTO;
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
    private String reportsPath;

    public String generateReport(DetalhesPedidoDTO pedidoDto) {

        Organizacao organizacao = organizacaoRepository
                .findByIdOrg(SecurityUtils.obterOrganizacaoId())
                .orElseThrow(() -> new AppException(
                        "Organização não encontrado",
                        "A organização é inválida",
                        HttpStatus.NOT_FOUND));

        try (InputStream jasperStream =
                     getClass().getResourceAsStream("/jasper/orderDetails/DetalhePedido.jasper")) {

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("cliente",      pedidoDto.getNomeFantasiaCliente());
            parameters.put("pedidoId",     pedidoDto.getIdPedido());
            parameters.put("dataCriacao",  pedidoDto.getDataCriacao());
            parameters.put("total",        pedidoDto.getPrecoTotal());
            parameters.put("organizacao",  organizacao.getNomeOrganizacao());

            JRBeanCollectionDataSource itensDataSource =
                    new JRBeanCollectionDataSource(pedidoDto.getItens());
            parameters.put("itensDataSource", itensDataSource);

            JasperPrint jasperPrint =
                    JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

            Path reportsDir = Paths.get(reportsPath);
            Files.createDirectories(reportsDir);

            String fileName = generateFileName(pedidoDto.getIdPedido().toString());
            Path   filePath = reportsDir.resolve(fileName);

            JasperExportManager.exportReportToPdfFile(jasperPrint, filePath.toString());

            return filePath.toString();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatório do pedido", e);
        }
    }

    private String generateFileName(String pedidoId) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("pedido_%s_%s.pdf", pedidoId, timestamp);
    }
}