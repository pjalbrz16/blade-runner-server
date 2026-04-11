package org.unamur.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.unamur.dto.InteractionDto;
import org.unamur.service.DotGeneratorService;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;



@Slf4j
public class DotGeneratorServiceImpl implements DotGeneratorService {

    public void createDotFile(List<InteractionDto> interactions, String outputPath) throws FileNotFoundException {
        StringBuilder dot = new StringBuilder();
        dot.append("digraph PR_Impact_Analysis {\n");
        dot.append("  rankdir=LR;\n"); // Left to Right flow
        dot.append("  node [shape=rectangle, style=filled, fontname=\"Helvetica\"];\n");

        // Define colors
        String prColor = "#FFB3BA";      // Light Red for PR files
        String externalColor = "#BAFFC9"; // Light Green for interacting classes

        for (InteractionDto edge : interactions) {
            // Node outside the PR
            dot.append(String.format("  \"%s\" [fillcolor=\"%s\"];\n",
                    edge.callerClass(), externalColor));

            // Node inside the PR
            dot.append(String.format("  \"%s\" [fillcolor=\"%s\"];\n",
                    edge.targetClass(), prColor));

            // The interaction (the edge)
            dot.append(String.format("  \"%s\" -> \"%s\" [label=\"calls\"];\n",
                    edge.callerClass(), edge.targetClass()));
        }

        dot.append("}");

        try (PrintWriter out = new PrintWriter(outputPath)) {
            out.println(dot.toString());
        }
    }
}
