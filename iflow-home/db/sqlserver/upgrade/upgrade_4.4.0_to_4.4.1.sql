INSERT INTO counter VALUES ('nodekey',0,GETDATE());

CREATE TABLE  dbo.active_node (
  nodekey varchar(50) NOT NULL,
  expiration DATETIME NOT NULL,
  PRIMARY KEY (nodekey)
);
insert into dbo.active_node(nodekey, expiration) values(1, get_date());

CREATE PROCEDURE get_next_nodekey 
  @retnodekey INT OUT
AS
BEGIN
		DECLARE @tmp INT
    set @retnodekey = 1
    select @tmp = value from counter where name='nodekey'
    update counter set value=(@tmp +1) where  name='nodekey'
    select @retnodekey = value from counter where name='nodekey'    
END
GO

CREATE TABLE  dbo.sharedobjectrefresh (
  id int NOT NULL IDENTITY,
  flowid int NOT NULL,
  PRIMARY KEY (id)
);