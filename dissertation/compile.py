import argparse
import subprocess

sources = ['text/doc_root.md',
           'text/title.md',
           'text/intro.md']


def pdf():
    """
    Converts the Markdown files into pdf format via Pandoc and pdflatex.
    """
    print("Generating pdf output")

    cmds = ['pandoc']
    cmds.extend(sources)

    options = ['-s', '-o', './bin/thesis.pdf']
    cmds.extend(options)

    print(subprocess.run(cmds, stdout=subprocess.PIPE, stderr=subprocess.PIPE))


def html():
    """
    Converts the Markdown files into a stand alone html site via Pandoc.
    """
    print("Generating html output")

    cmds = ['pandoc']
    cmds.extend(sources)

    options = ['-s', '-o', './bin/thesis.html']
    cmds.extend(options)

    print(subprocess.run(cmds, stdout=subprocess.PIPE, stderr=subprocess.PIPE))


def docx():
    """
    Converts the Markdown files into MS Word docx format via Pandoc.
    """
    print("Generating docx output")

    cmds = ['pandoc']
    cmds.extend(sources)

    options = ['-s', '-o', './bin/thesis.docx']
    cmds.extend(options)

    print(subprocess.run(cmds, stdout=subprocess.PIPE, stderr=subprocess.PIPE))


def main():
    parser = argparse.ArgumentParser(description="Compile the Markdown files into an output format via Pandoc.")
    build_type = parser.add_mutually_exclusive_group(required=True)
    build_type.add_argument("--html", action="store_true", help="Generate a stand alone html site.")
    build_type.add_argument("--pdf", action="store_true", help="Generate a single pdf file.")
    build_type.add_argument("--docx", action="store_true", help="Generate a MS Word docx file.")
    args = parser.parse_args()

    if args.html:
        html()
    elif args.pdf:
        pdf()
    elif args.docx:
        docx()


if __name__ == "__main__":
    main()
