package org.vgcs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.vgcs.model.ProcessedOrder;

import java.io.File;

@Component
public class JsonFileWriter implements ItemWriter<ProcessedOrder> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void write(Chunk<? extends ProcessedOrder> chunk) throws Exception {
        objectMapper.writeValue(new File("processed-orders.json"), chunk);
    }
}
