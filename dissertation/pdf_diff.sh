
olddir=~/projects/old_thesis_diff/dissertation
old=~/projects/old_thesis_diff/dissertation/dissertation.tex
new=~/projects/thesis/dissertation/dissertation.tex

cd $olddir
rm diff*
#cd ./diagrams
#python3 gen_pu_diagrams.py -pu /opt/apps/plantuml/plantuml.jar
#cd ..
latexdiff --flatten $old $new > diff.tex
pdflatex -synctex=1 -interaction=nonstopmode -shell-escape diff.tex
biber diff
#Run multiple times to resolve cross references
pdflatex -synctex=1 -interaction=nonstopmode -shell-escape diff.tex
pdflatex -synctex=1 -interaction=nonstopmode -shell-escape diff.tex
