import argparse
import subprocess
import os

'''
sources = ['text/algorithms.md',
           'text/abstract.md',
           'text/intro.md',
           'text/task_alloc.md']
'''

sources = ['text/title.md',
            'text/abstract.md',
            'text/toc.md',
            'text/dissertation.md']

#markdown_control_flags = ['--from=markdown+fenced_code_blocks', '--toc', '-s']
markdown_control_flags = ['--from=markdown+fenced_code_blocks', '-s']


def pdf():
   """
   Converts the Markdown files into pdf format via Pandoc and pdflatex.
   """
   print("Generating pdf output")
   #--variable classoption=twocolumn
   outputFlags = ['--to=latex', '-o', './bin/thesis.pdf',
#                  '--template=foo.blah',
                  #-H, Include in header
                  '-H', 'config.tex',
                  '--highlight-style', 'pygments',
                  '-V', 'fontsize=12pt']
#                  '-V', 'documentclass:report']
   runPandocCommand(outputFlags)


def html():
   """
   Converts the Markdown files into a stand alone html site via Pandoc.
   """
   print("Generating html output")
   outputFlags = ['--to=html', '-o', './bin/thesis.html']
   runPandocCommand(outputFlags)


def docx():
   """
   Converts the Markdown files into MS Word docx format via Pandoc.
   """
   print("Generating docx output")
   outputFlags = ['--to=docx', '-o', './bin/thesis.docx']
   runPandocCommand(outputFlags)

def runPandocCommand(outputFlags):
   """
   Executes the Pandoc utility with the global control flags and given output flags.
   """
   cmds = ['pandoc']
   cmds.extend(sources)
   cmds.extend(markdown_control_flags)
   cmds.extend(outputFlags)
   print("Executing: " + ' '.join(cmds))
   print(subprocess.run(cmds, stdout=subprocess.PIPE, stderr=subprocess.PIPE))

def main():
   if(not os.path.isdir('./bin')):
      os.mkdir('./bin')   

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
