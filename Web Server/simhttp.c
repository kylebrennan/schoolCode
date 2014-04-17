#include <sys/socket.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <arpa/inet.h>
#include <time.h>
#include <sys/time.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/types.h>

#define	LISTENQ	1024
#define MAXLINE	4096
#define	SA struct sockaddr

/*
 *
 * Kyle Brennan
 * Rashmili Vemula
 *
 */

int dirLen = 1; //hold the original length of the directory

void doConnectStuff(int sock, char *dir);
int fileInclude(char *holder);
int dotsInclude(char *holder);
int getEnding(char *holder, char *end);
void sendResponse(int code, char *dir, char *type, char *method, int sock, int get);

int getEnding(char *holder, char *end){
	int i;
	for(i=0;i<strlen(end);i++){
		if(end[i] != holder[strlen(holder)-strlen(end)+i])
			return 0;
	}
	return 1;
}

//determine if there is something after the slash
int fileInclude(char* holder){
	int dot = 0, slash = 0;
	
	int i;
	for(i=0;i<strlen(holder)+1;i++){
		if(holder[i] == '/' && dot == 1){
			if(slash == 1)
				dot = 0;
			else
				slash = 1;
		}
		if(holder[i] == '.')
			dot = 1;
	}
	
	return dot;
}

//don't let it go to back directories
int dotsInclude(char *holder){
	char *test = NULL;
	test = strstr(holder, "../");
	if(test != NULL)
		return 1;
	return 0;
}

void sendResponse(int code, char *dir, char *type, char *method, int sock, int get){
	char *response = (char *)malloc(1000000);
	char *servTime = (char *)malloc(64);
	char *responseTime = (char *)malloc(64);
	char *timeMod = (char *)malloc(64);
	
	time_t current = time(NULL);
	strftime(servTime, 64, "%d %b %Y %H:%M", localtime(&current));
	
	if(code != 404)
		dir += dirLen+1;
	strcat(response, "HTTP/1.1 ");
	switch(code){
		case 200:
			strcat(response, "200 OK\r\n");
			printf("%s\t%s\t%s\t200\n",method,dir,servTime);
			break;
		case 400:
			strcat(response, "400 BAD\r\n");
			printf("%s\t%s\t%s\t400\n",method,dir,servTime);
			strcat(response, "Connection: Close\r\nLearn how to write!\r\n\r\n");
			write(sock, response, strlen(response));
			exit(0);
			break;
		case 403:
			strcat(response, "403 BAD\r\n");
			printf("%s\t%s\t%s\t403\n",method,dir,servTime);
			break;
		case 404:
			strcat(response, "404 BAD\r\n");
			printf("%s\t%s\t%s\t404\n",method,dir,servTime);
			break;
		case 405:
			strcat(response, "405 BAD\r\n");
			printf("%s\t%s\t%s\t405\n",method,dir,servTime);
			break;
	}
	
	if(code != 404)
		dir -= dirLen+1;
	strcat(response, "Connection:\tClose\r\n");
	strcat(response, "Date:\t");
	strftime(responseTime, 64, "%a, %d %b %Y %H:%M:%S",localtime(&current));
	strcat(response, responseTime);
	strcat(response, "\r\n");
	
	strcat(response, type);
	strcat(response, "Server:\tKyleAndRashMakeAServer/1.1\r\n");
	
	if(code != 403 && code != 404){
		struct stat fileStat;
		stat(dir, &fileStat);
		time_t lastMod = fileStat.st_mtime;
		strcat(response, "Last-Modified:\t");
		strftime(timeMod, 64, "%a, %d %b %Y %H:%M:%S",localtime(&lastMod));
		strcat(response, timeMod);
		strcat(response, "\r\n");
	
		strcat(response, "Content-Length:\t");
	
		int size = fileStat.st_size;
		char fileSize[128];
		sprintf(fileSize, "%d",size);
		strcat(response, fileSize);
		strcat(response, "\r\n");
	}
	
	strcat(response, "\r\n");
	
	//get contents of file
	if(get != 0){
		FILE *fp;
		long length;
		int size = strlen(response);
		
		fp = fopen(dir, "r");
		if(fp){
			fseek(fp, 0, SEEK_END);
			length = ftell(fp);
			fseek(fp, 0, SEEK_SET);
			fread(response + size, 1, length, fp);
			fclose(fp);
		}
		write(sock, response, size+length);
	}
	else
		write(sock, response, strlen(response));

	close(sock);
}



void doConnectStuff(int sock, char *dir){
	int e400=0, e403=0, e404=0, e405=0;
	char recvline[MAXLINE+1];
	char current[MAXLINE+1];
	char *buf = (char *)malloc(MAXLINE+1);
	bzero(buf,MAXLINE);
	
	int newLinePos = 0;
	
	int n = read(sock, recvline, MAXLINE);
	recvline[n-1] = '\0'; //putting null character on end of message
	
	strcat(buf, recvline);
	
	char *check = strstr(buf, "HTTP/1.1");
	if(check == NULL)
		e400 = 1;
	
	char *holder = NULL;
	char *holder405 = NULL;
	
	char type[64];
	bzero(type, 64);
	strcat(type, "Content-Type:\t");
	
	char *name = (char *)malloc(64);
	
	int get = 0, head = 0, first = 1, host = 0, oops = 0;
	int nameLen;
	while(strlen(buf)>1){
		for( ; ; ){
			if(buf[newLinePos] == 10 || newLinePos >= strlen(buf))
				break;
			newLinePos++;
		}
		strncat(current, buf, newLinePos-1); //copy into current, but not the '\0'
		newLinePos = 0;
		
		buf = strstr(buf, "\n");
		
		//malformed request
		if(buf == NULL){
			if(get == 1)
				sendResponse(400, dir, type, "GET", sock, 0);
			else if(head == 1)
				sendResponse(400, dir, type, "HEAD", sock, 0);
		}
		buf += 1;	//move past the \n
		
		//we now have the current line
		if(first == 1){
			holder = strstr(current, "GET");
			if(holder != NULL){
				holder += 4;
				get = 1;
			}
			else{
				holder = strstr(current, "HEAD");
				if(holder != NULL){
					holder += 5;
					head = 1;
				}
				else{
					e405 = 1;
					
					//get the method name
					holder = (char *)malloc(strlen(current));
					bzero(holder, strlen(holder));
					strcat(holder, current);
					
					holder405 = (char *)malloc(strlen(holder));
					bzero(holder405, strlen(holder405));
					strcat(holder405, holder);
					
					newLinePos = 0;
					for( ; ; ){
						if(holder[newLinePos] == ' ')
							break;
						newLinePos++;
					}
					holder += newLinePos + 1;
					holder405[newLinePos] = '0';
				}
			}
			
			//get the name of an undiscoverable file
			memcpy(name, holder, strlen(holder)-9);
			name += 1;
			nameLen = strlen(name);

			first = 0;
			for( ; ; ){
				if(newLinePos+1 > strlen(holder)){
					if(get == 1){
						sendResponse(404, name, type, "GET", sock, 0);
						bzero(current, strlen(current));
						oops = 1;
						break;
					}
					else if(head == 1){
						sendResponse(404, name, type, "HEAD", sock, 0);
						bzero(current, strlen(current));
						oops = 1;
						break;
					}
				}
				
				if(holder[newLinePos] == '/' && holder[newLinePos+1] == ' ')
					break;
				newLinePos++;
			}
			holder[newLinePos] = 0; //we don't want the '/' to be there
			
			if(strlen(holder) != 0)
				strcat(dir, "/");
			if(fileInclude(holder) == 0)
				strcat(holder, "/index.html");
			if(dotsInclude(holder) == 1)
				e403 = 1;	
				
			if(getEnding(holder, ".css"))
				strcat(type, "text/css");
			else if(getEnding(holder, ".html") || getEnding(holder, ".htm"))
				strcat(type, "text/html");
			else if(getEnding(holder, ".js"))
				strcat(type, "application/javascript");
			else if(getEnding(holder, ".txt"))
				strcat(type, "text/plain");
			else if(getEnding(holder, ".jpg"))
				strcat(type, "image/jpeg");
			else if(getEnding(holder, ".pdf"))
				strcat(type, "application/pdf");
			else
				strcat(type, "application/octet-stream");
			strcat(type, "\r\n");
			
			strcat(dir, holder);
			
			if(access(dir, F_OK) == -1){
				if(get == 1){
					sendResponse(404, dir, type, "GET", sock, 0);
					oops = 1;
					bzero(current, strlen(current));
				}
				else{
					sendResponse(404, dir, type, "HEAD", sock, 0);
					oops = 1;
					bzero(current, strlen(current));
				}
			}
			
			if(e400 == 1){
				if(get == 1){
					sendResponse(400, dir, type, "GET", sock, 0);
					oops = 1;
					bzero(current, strlen(current));
				}
				else{
					sendResponse(400, dir, type, "HEAD", sock, 0);
					oops = 1;
					bzero(current, strlen(current));
				}
			}
			else if(e403 == 1){
				if(get == 1){
					sendResponse(403, dir, type, "GET", sock, 0);
					oops = 1;
					bzero(current, strlen(current));
				}
				else{
					sendResponse(403, dir, type, "HEAD", sock, 0);
					oops = 1;
					bzero(current, strlen(current));
				}
			}
			else if(e405 == 1){
				sendResponse(405, dir, type, holder405, sock, 0);
				oops = 1;
				bzero(current, strlen(current));
			}
		}
		
		if(host == 0){
			holder = strstr(current, "Host");
			if(holder != NULL)
				host = 1;
		}
		
		bzero(current, strlen(current));
	}
	
	if(host == 0){
		fprintf(stderr,"We never got a host\n");
		sendResponse(400, dir, type, "GET", sock, 0);
	}
	
	//we made it through!
	if(get == 1 && oops == 0)
		sendResponse(200, dir, type, "GET", sock, 1);
	else if(head == 1 && oops == 0)
		sendResponse(200, dir, type, "HEAD", sock, 0);
}

int main(int argc, char **argv){
	int port = 8080;
	
	int c;
	while((c = getopt(argc, argv, "p:")) != -1){
		if(c == 'p')
			port = atoi(optarg);
	}
	
	char* dir = argv[argc-1];
	dirLen = strlen(dir);
	char dirHolder[64];
	strcpy(dirHolder, dir);
	
	if(access (dir, F_OK) == -1) {
		fprintf(stderr,"Directory not available. Exiting...\n");
		exit(0);
	}
	
	int listenfd, connfd;
	if((listenfd = socket(AF_INET, SOCK_STREAM, 0)) < 0){
		printf("Socket not created! Exiting...\n");
		exit(0);	
	}
	
	struct sockaddr_in servaddr;
	bzero(&servaddr, sizeof(servaddr));
	servaddr.sin_family = AF_INET;
	servaddr.sin_addr.s_addr = htonl(INADDR_ANY);
	servaddr.sin_port = htons(port);
	
	//make it easier for a closed connection to start again
	//with same address
	int reuse = 1;
	setsockopt(listenfd, SOL_SOCKET, SO_REUSEADDR, &reuse, sizeof(reuse));
	
	if ((bind(listenfd, (SA *) &servaddr, sizeof(servaddr))) < 0){
		printf("Binding error. Exiting...\n");
		exit(0);
	}	

	if ((listen(listenfd, LISTENQ)) < 0){
		printf("Listening error. Exiting...\n");
		exit(0);
	}
	
	for( ; ; ){
		fflush(stdout);
		connfd = accept(listenfd, (SA *) NULL, NULL);
		
		doConnectStuff(connfd, dir);
		bzero(dir,strlen(dir));
		strcat(dir, dirHolder);
		close(connfd);
	}
}
