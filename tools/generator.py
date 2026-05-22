#!/usr/bin/env python3
from __future__ import annotations

import pandas
import time
import os
import subprocess
import argparse
import sys
import re

class Timer:
  def __init__(self, indent: int = 0) -> None:
    self.clock = time.time()
    self.indent = indent

  def branch(self) -> Timer:
    return Timer(self.indent + 1)

  def round(self, msg: str):
    nclock = time.time()
    diff = nclock - self.clock
    print("%s| %s | Elapsed %s s" % ("  " * self.indent, msg, diff))
    self.clock = time.time()
    return diff

  def clear(self):
    self.clock = time.time()

def exec_cmd(cmd: str):
  print(">", cmd)
  assert 0 == os.system(cmd)

def get_output(cmd: str):
  print("]", cmd)
  status, output = subprocess.getstatusoutput(cmd)
  assert 0 == status
  return output

def generate_code_function(number: int, opts: int = 4):
  header = "  func_%s_%s(" % (number, opts)

  middle = """  ) {
    def allneg = true;
  """

  footer = """
    if (allneg)
      this.abort();
  }
  """

  snippets, vars = [], []
  for i in range(1, number + 1):
    snippet, expr = generate_code_block(i, opts)
    snippets.append(snippet)
    vars += expr
  listing = []
  listing.append(header)
  listing.append(",\n".join(["    " + e for e in vars]))
  listing.append(middle)
  listing.append("\n".join(snippets))
  listing.append(footer)
  
  return "\n".join(listing)

def generate_code_block(num: int = 0, opts: int = 4):
  assert opts > 0
  listing = []

  var_name = 'v%s' % num
  exprs = []

  # listing.append("// code block %s" % num)
  exprs.append("i%s0" % num)
  listing.append("    def %s = %s;" % (var_name, exprs[-1]))
  exprs.append("i%s1" % num)
  listing.append("if (%s >= 0) {" % exprs[-1])
  listing.append("  %s = 1;" % var_name)
  for i in range(2, opts + 1):
    exprs.append("i%s%s" % (num, i))
    listing.append("} else if (%s >= 0) {" % exprs[-1])
    listing.append("  %s = 1;" % var_name)
  listing.append("}")
  listing.append("if (%s >= 0) {" % var_name)
  listing.append("  allneg = false;")
  listing.append("}")

  return "\n    ".join(listing), exprs

def main():
  argument_parser = argparse.ArgumentParser()
  argument_parser.add_argument('-bb', '--blocks-begin', type=int, help='Min NoB', default=2)
  argument_parser.add_argument('-bs', '--blocks-step', type=int, help='Step NoB', default=1)
  argument_parser.add_argument('-be', '--blocks-end', type=int, help='Max NoB', default=2)
  argument_parser.add_argument('-ob', '--options-begin', type=int, help='Min NoO', default=5)
  argument_parser.add_argument('-os', '--options-step', type=int, help='Step NoO', default=1)
  argument_parser.add_argument('-oe', '--options-end', type=int, help='Max NoO', default=5)
  args = argument_parser.parse_args(sys.argv[1:])

  bb, bs, be = args.blocks_begin, args.blocks_step, args.blocks_end
  ob, os, oe = args.options_begin, args.options_step, args.options_end

  assert (bb > 0) and (bs > 0) and (be > 0)
  assert (ob > 0) and (os > 0) and (oe > 0)

  timer = Timer()
  data = {
    'NoB': [], # Number of Blocks
    'NoO': [], # Number of Options
    'ToG': [], # Time of Generation
    'ToC': [], # Time of Compilation
    'ToE': [], # Time of Execution
    'NoP': [], # Number of Paths
  }
  
  listing = []
  listing.append("class Generated {")
  for NoB in range(bb, be + 1, bs):
    for NoO in range(ob, oe + 1, os):
      listing.append(generate_code_function(number=NoB, opts=NoO - 1))
  listing.append("}")
  with open("inputs/example3.imp", 'w') as file:
    file.write("\n".join(listing))

if __name__ == '__main__':
  main()
