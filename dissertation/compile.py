import argparse
import subprocess

'''
sources = ['text/algorithms.md',
           'text/abstract.md',
           'text/intro.md',
           'text/task_alloc.md']
'''

sources = ['text/algorithms.md']

markdown_from = '--from=markdown+fenced_code_blocks'


def pdf():
    """
    Converts the Markdown files into pdf format via Pandoc and pdflatex.
    """
    print("Generating pdf output")

    cmds = ['pandoc']
    cmds.extend(sources)

    options = [markdown_from, '--to=latex', '-s', '-o', './bin/thesis.pdf']
    cmds.extend(options)
    print("Executing: " + ' '.join(cmds))
    print(subprocess.run(cmds, stdout=subprocess.PIPE, stderr=subprocess.PIPE))


def html():
    """
    Converts the Markdown files into a stand alone html site via Pandoc.
    """
    print("Generating html output")

    cmds = ['pandoc']
    cmds.extend(sources)

    options = [markdown_from, '--to=html', '-s', '-o', './bin/thesis.html']
    cmds.extend(options)

    print(subprocess.run(cmds, stdout=subprocess.PIPE, stderr=subprocess.PIPE))


def docx():
    """
    Converts the Markdown files into MS Word docx format via Pandoc.
    """
    print("Generating docx output")

    cmds = ['pandoc']
    cmds.extend(sources)

    options = [markdown_from, '--to=docx', '-s', '-o', './bin/thesis.docx']
    cmds.extend(options)

    print(subprocess.run(cmds, stdout=subprocess.PIPE, stderr=subprocess.PIPE))


def main():
    parser = argparse.ArgumentParser(description="Compile the Markdown files into an output format via Pandoc.")
    build_type = parser.add_mutually_exclusive_group(required=True)
    build_type.add_argument("--all", action="store_true", help="Generate all supported formats.")
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
    elif args.all:
        html()
        pdf()
        docx()


if __name__ == "__main__":
    main()
