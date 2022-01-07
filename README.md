# Java Phylogenetic Tree Collapser [![license](https://img.shields.io/badge/license-Apache%202.0-brightgreen)](https://github.com/pegi3s/phylogenetic-tree-collapser) [![dockerhub](https://img.shields.io/badge/hub-docker-blue)](https://hub.docker.com/r/pegi3s/phylogenetic-tree-collapser)
> **Java Phylogenetic Tree Collapser** (`treecollapse`) is a Java program to collapse phylogenetic trees in Newick format. It is the core of the [**Phylogenetic Tree Collapser**](https://github.com/pegi3s/phylogenetic-tree-collapser) (PTC) utility.

# Using the **Phylogenetic Tree Collapser** (PTC) utility

This Java program is the core of the [**Phylogenetic Tree Collapser**](https://github.com/pegi3s/phylogenetic-tree-collapser) (PTC) utility, a tool providing a simple and flexible way to collapse phylogenetic trees using taxonomic information.

Thus, the recommended way of using it is trough **PTC**, for which a Docker image is available [at this Docker Hub repository](https://hub.docker.com/r/pegi3s/phylogenetic-tree-collapser) with detailed information on the collapsing procedure and examples.

# Project build

To build the projet simply run the following command: `mvn clean install`. The corresponding JAR file will appear at `target` (e.g.: `treecollapse-1.0.0-jar-with-dependencies.jar` for the 1.0.0 version).

# The `collapse-tree` command

The `collapse-tree` command uses the following input files:
- `--input`: a phylogenetic tree in Newick format.
- `--sequence-mapping`: a tab-delimited file mapping each sequence name to its species. This file must have two colums: the first containing the sequence names of the input tree and the second one containing their corresponding species.
- `--taxonomy`: a plain-text file with the input taxonomy file. This file must have one line for each species with their taxonomy terms separated by semi-colons.
- `--taxonomy-stop-terms`: a plain-text file with the taxonomy stop terms file (one line for each stop term).

In addition, the `--output-type` specifies the type of the output phylogenetic tree: `cladogram` or `phylogram`. It produces two output files:
- `--output`: the output phylogenetic tree file in Newick format.
- `--output-collapsed-nodes`: a tab-delimited file with the collapsed nodes.

Example of usage:

```java -jar treecollapse-1.0.0-jar-with-dependencies.jar collapse-tree \
    --input /path/to/input \
    --sequence-mapping /path/to/sequence-mapping \
    --taxonomy /path/to/taxonomy \
    --taxonomy-stop-terms /path/to/taxonomy-stop-terms \
    --output /path/to/output \
    --output-type cladogram \
    --output-collapsed-nodes /path/to/output-collapsed-nodes
```

