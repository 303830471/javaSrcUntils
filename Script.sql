--<ScriptOptions statementTerminator=";"/>

CREATE TABLE chat_log (
	id INT NOT NULL,
	open_id VARCHAR(30) NOT NULL,
	create_time VARCHAR(20) NOT NULL,
	req_msg VARCHAR(2000) NOT NULL,
	resp_msg VARCHAR(2000) NOT NULL,
	chat_category INT,
	PRIMARY KEY (id)
);

