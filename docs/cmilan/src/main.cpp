#include "parser.h"
#include <iostream>
#include <cstdlib>

using namespace std;

void printHelp()
{
	cout << "Usage: cmilan input_file" << endl;
}

int main(int argc, char** argv)
{
	/*
	if(argc < 2) {
		printHelp();
		return 1;
	}*/
	argv[1] = "C:\\Users\\Егор\\Desktop\\milan-compilers\\cmilan\\src\\cmilan_vs2011\\Debug\\switch.mil";
	Parser p(argv[1]);
	p.parse();
	return 0;
}