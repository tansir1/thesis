#cd ./diagrams
#python3 gen_pu_diagrams.py -pu /opt/apps/plantuml/plantuml.jar
#cd ../../python_scripts
#python3 objectives_charts.py
#cd ../dissertation
rm dissertation.pdf
rm -rf _minted-dissertation
rm *.aux
rm *.bbl
rm *.bcf
rm *.blg
rm *.lof
rm *.log
rm *.lot
rm *.out
rm dissertation.run.xml
rm *.gz
rm *.tdo
rm *.toc
pdflatex -synctex=1 -interaction=nonstopmode -shell-escape dissertation.tex
biber dissertation
#Run multiple times to resolve cross references
pdflatex -synctex=1 -interaction=nonstopmode -shell-escape dissertation.tex
pdflatex -synctex=1 -interaction=nonstopmode -shell-escape dissertation.tex
