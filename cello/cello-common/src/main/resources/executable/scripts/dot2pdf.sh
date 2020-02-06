#!/bin/bash
filename=`basename -s .dot ${1}`
dot -Tpdf ${1} -o ${filename}.pdf
