package com.finnex.finance_app.domain.transactions.export;

import com.finnex.finance_app.domain.transactions.entity.Transaction;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
@Component
public class TransactionExcelExporter {
    public byte[] export(List<Transaction> transactions)throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Transactions");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Transaction ID");
        headerRow.createCell(1).setCellValue("Date");
        headerRow.createCell(2).setCellValue("Account");
        headerRow.createCell(3).setCellValue("Type");
        headerRow.createCell(4).setCellValue("Category");
        headerRow.createCell(5).setCellValue("Merchant");
        headerRow.createCell(6).setCellValue("Amount");
        headerRow.createCell(7).setCellValue("Status");

        int rowNumber = 1;
        for(Transaction transaction:transactions){
            Row row = sheet.createRow(rowNumber++);
            row.createCell(0).setCellValue(transaction.getId().toString());
            row.createCell(1).setCellValue(transaction.getTransactionDate().toString());
            row.createCell(2).setCellValue(transaction.getAccount().getAccountName());
            row.createCell(3).setCellValue(transaction.getType().name());
            row.createCell(4).setCellValue(transaction.getCategory().name());
            row.createCell(5).setCellValue(transaction.getMerchantName());
            row.createCell(6).setCellValue(transaction.getAmount().doubleValue());
            row.createCell(7).setCellValue(transaction.getStatus().name());

        }
        for (int i = 0; i < 8; i++) {
            sheet.autoSizeColumn(i);
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return  outputStream.toByteArray();
    }
}
