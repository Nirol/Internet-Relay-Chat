
#include <stdio.h>
#include "boost/thread.hpp"
#include "ConnectionHandler.h"
using namespace std;

bool numericReply(string& msg){
	if (msg.length() < 3) return false;

	const char* c = msg.c_str();

	for(int i = 0; i<3; i++){
		if (*c < '0' || *c > '9') return false;
		c++;
	}

	if (*c != '\0' && *c != ' ') return false;

	return true;
}

void stdinThread(ConnectionHandler* connectionHandler)
{
	bool out = false;

	while (! out) {
		const short bufsize = 1024;
		char buf[bufsize];
		std::cin.getline(buf, bufsize);
		string line(buf);
		if (!connectionHandler->sendLine(line)) {
			printf("Disconnected. Exiting...\n");
			out = true;
			break;
		}

		if (line.length()>=4 && line.substr(0,4) == "QUIT"){
			out = true;
			break;
		}
	}

}

int main (int argc, char *argv[]) {

	short port = 6667;


	if (argc < 2) {
		std::cerr << "Parameter required: IPv4 of server registered to port " << port << "\n";
		return -1;
	}
	string host = argv[1];

	ConnectionHandler connectionHandler(host, port);
	if (!connectionHandler.connect()) {
		std::cerr << "Cannot connect to " << host << ":" << port << "\n";
		return 1;
	}


	boost::thread inThread(stdinThread, &connectionHandler);

	bool out = false;

	while (! out) {
		string msg;
		if (!connectionHandler.getLine(msg)) {
			std::cout << "Disconnected. Exiting...\n" << std::endl;
			break;
		}

		int len=msg.length();
		// A C string must end with a 0 char delimiter.  When we filled the answer buffer from the socket
		// we filled up to the \n char - we must make sure now that a 0 char is also present. So we truncate last character.
		msg.resize(len-1);
		if (msg == "QUIT"){
			printf("Server acknowledged QUIT command");
			out = true;
			break;
		} else if(numericReply(msg)){
			//printf("Reply: %s - %d bytes\n",msg.c_str(),len);
			printf("%s\n",msg.c_str()+4);
		} else {
			printf("%s\n",msg.c_str());
		}
	}
	return 0;
}
