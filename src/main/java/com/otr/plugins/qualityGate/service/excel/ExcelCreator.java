package com.otr.plugins.qualityGate.service.excel;

import com.otr.plugins.qualityGate.service.handler.Handler;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import static java.awt.Color.lightGray;

@Component
@Slf4j
public class ExcelCreator {

    private static final List<String> COLUMNS = List.of(
            "id", "title", "message", "issues", "key", "type", "status", "description"
    );

    public String create(Map<Handler.ResulType, Handler.Result> content) {
        final String fileName = "src/main/resources/" + UUID.randomUUID() + ".xls";
        try (HSSFWorkbook workbook = new HSSFWorkbook()) {
            content.forEach((k, v) -> {
                final HSSFSheet sheet = workbook.createSheet(k.name());
                // счетчик для строк
                int rowNum = 0;

                // создаем подписи к столбцам (это будет первая строчка в листе Excel файла)
                Row row = sheet.createRow(rowNum);
                final CellStyle style = createHeaderStyle(workbook);
                final CellStyle rowStyle = createRowStyle(workbook);

                for (int i =0; i < COLUMNS.size(); i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellStyle(style);
                    cell.setCellValue(COLUMNS.get(i));
                    // autosize
                    sheet.autoSizeColumn(i);
                }

                // заполняем лист данными
                for (Map<String, String> data : v.getContent()) {
                    createSheetHeader(rowStyle, sheet, ++rowNum, data);
                }

                try (FileOutputStream out = new FileOutputStream(fileName)) {
                    workbook.write(out);
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            });


        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }

        return fileName;

    }

    private void createSheetHeader(CellStyle cellStyle, HSSFSheet sheet, int rowNum, Map<String, String> data) {
        Row row = sheet.createRow(rowNum);
        IntStream.range(0, COLUMNS.size()).forEach(i -> {
            Cell cell = row.createCell(i);
            cell.setCellStyle(cellStyle);
            String content = data.get(COLUMNS.get(i));
            cell.setCellValue(content);
        });
    }


    private CellStyle createRowStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        //style.setWrapText(true);
        return style;
    }
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Courier New");
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillBackgroundColor(new HSSFColor(0, 1, lightGray));
        return style;
    }
}
