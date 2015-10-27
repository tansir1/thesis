import argparse


def pdf():
    """
    Converts the Markdown files into pdf format via Pandoc and pdflatex.
    """
    print("Generating pdf output")

def html():
    """
    Converts the Markdown files into a stand alone html site via Pandoc.
    """
    print("Generating html output")

def docx():
    """
    Converts the Markdown files into MS Word docx format via Pandoc.
    """
    print("Generating docx output")

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
