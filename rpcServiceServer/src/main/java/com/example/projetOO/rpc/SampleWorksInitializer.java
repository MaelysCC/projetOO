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
                    new Work(
                        "Witch Hat Atelier", 
                        WorkType.MANGA, 
                        "Kamome Shirahama", 
                        "A young girl discovers the world of magic and dreams of becoming a witch.", 
                        Status.ONGOING
                    ),
                    new Work(
                        "Lord of Mysteries", 
                        WorkType.NOVEL, 
                        "Cuttlefish That Loves Diving", 
                        "Chinese salaryman Zhou Mingrui gets transmigrated to a steampunk Victorian world through a luck enhancing ritual, into the body of Klein Moretti. As the Fool, he will deceive the world and eventually ascend to divinity, but not without cost.", 
                        Status.COMPLETED
                    ),
                    new Work(
                        "The Nebula's Civilization",
                        WorkType.NOVEL,
                        "Wirae",
                        "After being the first to unlock every achivement of the game Aldin, Choi Sung Won received an invitation with other players to join the world of Aldin as divinity and defied each other until only one stand as the only god",
                        Status.COMPLETED
                    ),

                    new Work(
                        "Tears of a Jester",
                        WorkType.WEBCOMIC,
                        "Albretch",
                        "Two princess, one throne. Life is hard for the court jester, not only do I have to serve and distract every noble but i also ended up in the crossfire for the crown.",
                        Status.COMPLETED
                    ),

                    new Work(
                        "Mononoke",
                        WorkType.ANIME,
                        "Kenji Nakamura",
                        "A medecine seller traveling Japan while fighting and exorcist mononoke, demon born from human resentment.",
                        Status.ONGOING
                    ),

                    new Work(
                        "Got Dropped Into a Ghost Story, Still Gotta Work",
                        WorkType.NOVEL,
                        "DS Back (aka Baek Deoksu)",
                        "Kim Soleum, a Korean salaryman, fan on the horror univers of the common work of ghost story, Daydream Inc. One day go get merch in the new popup store and ends up in the work of the serie, during the initiation of the new recruit of Daydream Inc.",
                        Status.ONGOING
                    ),

                    new Work(
                        "Comte Cain",
                        WorkType.MANGA,
                        "Kaori Yuki",
                        "A curse name with a heavy meaning, a curse as old as humanity, and a family with a story darker than it seems.",
                        Status.COMPLETED
                    ),

                    new Work(
                        "God Child",
                        WorkType.MANGA,
                        "Kaori Yuki",
                        "Sequel of Comte Cain, Cain follow the trace of Delilah, a mysterious organisation lead by The Doctor, a mysterious man wishing for the comte demise.",
                        Status.COMPLETED
                    ),

                    new Work(
                        "Detective Conan",
                        WorkType.ANIME,
                        "Gosho Aoyama",
                        "We follow Shinichi Kudo, a teen detective seen as the Heisei Holmes. After a incident in an amusement park with two mans in black, he ends up in his 10 years old body. He will have to find a cure while continuing invistigating the mysterious organization.",
                        Status.ONGOING
                    ),

                    new Work(
                        "Academy's Undercover Professor",
                        WorkType.WEBCOMIC,
                        "Sayen",
                        "His new cover, Ludger Cherish, is a professor at the prestigious Ceoren Academy. However, this seemingly ordinary professor was part of a secret society with unclear motive. To protect his identity, he must thread carefully and not get his cover blown.",
                        Status.DROPPED
                    ),

                    new Work(
                        "Jibaku Shounen Hanako-kun",
                        WorkType.MANGA,
                        "AidaIro",
                        "Nene Yashiro summons Hanako-san, the ghost of a girl who haunts the girl's bathroom at her school and can grant wishes for a price. However, Hanako-kun is different from the rumor and she become spriritually bound to him and helps him as his assistant.",
                        Status.ONGOING
                    ),

                    new Work(
                        "Marionetta",
                        WorkType.WEBCOMIC,
                        "Miriam Bonastre Tur",
                        "Julia is happy with her life but when her best friend Kamille convinces her to visit a travelling circus. They get thrust into a world full of secrets, magic, and death where Julia is presented with the most horrific of deals in order to survive.",
                        Status.ON_HOLD
                    ),

                    new Work(
                        "Death Note",
                        WorkType.ANIME,
                        "Tsugumi Ohba",
                        "Light Yagami finds the death note, a notebook where writing a person's name can kill them. He becomes a serial killer following his own sense of justice, with the police trying to find his identity with a detective named L.",
                        Status.COMPLETED
                    )


                
                
            );
            workRepository.saveAll(samples);
            logger.info("Loaded {} sample works into the in-memory catalogue", samples.size());
        };
    }
}
