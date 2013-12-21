ALTER TABLE activity ADD COLUMN previoususerid VARCHAR(100) AFTER userid;
ALTER TABLE activity_history ADD COLUMN previoususerid VARCHAR(100) AFTER userid;
ALTER TABLE activity_history ADD COLUMN previoususerid VARCHAR(100) AFTER userid;
ALTER TABLE activity_archive ADD COLUMN previoususerid VARCHAR(100) AFTER userid;

DROP VIEW activity_delegated;
CREATE VIEW activity_delegated
    (hierarchyid, userid, previoususerid, pid, subpid, ownerid, flowid, created, type, started, archived, 
    status, notify, priority, description, url,profilename, requested, responded, read_flag,mid) as
    select H.hierarchyid, H.userid, A.previoususerid, A.pid, A.subpid, A.userid as ownerid, A.flowid, A.created, 
    A.type, A.started, A.archived, A.status, A.notify, A.priority, A.description, A.url, A.profilename,
    H.requested, H.responded, A.read_flag, A.mid
    from activity A, activity_hierarchy H
    where ((A.userid = H.ownerid and H.slave=1) or (A.userid = H.userid and slave=0)) 
    and A.flowid = H.flowid and H.pending=0 and A.delegated <> 0;

DROP TRIGGER trigger_activity_previoususer;
DELIMITER //
CREATE TRIGGER trigger_activity_previoususer
BEFORE INSERT ON `activity` FOR EACH ROW
BEGIN
DECLARE previoususerid varchar(100);
SELECT currentuser into previoususerid from process WHERE flowid=NEW.flowid and pid = NEW.pid;
if (previoususerid = NEW.userid) then
    SET NEW.previoususerid = '';
else
    SET NEW.previoususerid = previoususerid;
end if;
END;
//