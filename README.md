# Joern Default Batteries

## Installation

Make sure Joern is installed, then run

```
./install.sh
```

This will install the following script bundles:

* cscanner - a Vulnerability scanner for C code

## Running Scanners

You can run a script bundle as follows:

```
joern --src path/to/code --run <bundlename> --param k1=v1,...
```

For example,

```
joern --src path/to/code --run cscanner
```

runs the C scanner on the code at `path/to/code`.

