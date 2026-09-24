package com.example.projetOO.rpc;

import com.example.projetOO.entities.Status;
import com.example.projetOO.entities.Work;
import com.example.projetOO.entities.WorkType;
import com.example.projetOO.repository.WorkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SampleWorksInitializer {
    private static final Logger logger = LoggerFactory.getLogger(SampleWorksInitializer.class);

    @Bean
    ApplicationRunner seedSampleWorks(WorkRepository workRepository) {
        return args -> {
            if (workRepository.count() != 0) return;

            List<Work> samples = List.of(
                    new Work("Witch Hat Atelier", WorkType.MANGA, "Kamome Shirahama", "A young girl discovers the world of magic and dreams of becoming a witch.", Status.ONGOING)
            );
            workRepository.saveAll(samples);
            logger.info("Loaded {} sample works into the in-memory catalogue", samples.size());
        };
    }
}
