# Hadoop HDFS Commands Reference (Teaching Notes)

Covers: HDFS user (file system) commands, HDFS admin (`dfsadmin`) commands,
`fsck`, and the offline tools `oiv` / `oev` for inspecting the **fsimage** and
**edit logs**. Sample outputs included so students know what to expect.

---

## 1. HDFS User Commands (`hdfs dfs`)

General syntax: `hdfs dfs -<option> <args>` (or the older `hadoop fs -<option>`)

| Command | Purpose |
|---|---|
| `hdfs dfs -ls /` | List contents of a directory |
| `hdfs dfs -ls -R /user` | Recursive listing |
| `hdfs dfs -mkdir /user/student1` | Create a directory |
| `hdfs dfs -put localfile.txt /user/student1/` | Upload file to HDFS |
| `hdfs dfs -copyFromLocal localfile.txt /user/student1/` | Same as `-put` |
| `hdfs dfs -get /user/student1/file.txt .` | Download file from HDFS |
| `hdfs dfs -copyToLocal /user/student1/file.txt .` | Same as `-get` |
| `hdfs dfs -cat /user/student1/file.txt` | Print file content |
| `hdfs dfs -tail /user/student1/file.txt` | Show last KB of file |
| `hdfs dfs -rm /user/student1/file.txt` | Delete a file |
| `hdfs dfs -rm -r /user/student1/dir` | Delete directory recursively |
| `hdfs dfs -du -h /user` | Disk usage per file/dir (human readable) |
| `hdfs dfs -df -h` | Overall HDFS capacity/usage |
| `hdfs dfs -chmod 755 /user/student1/file.txt` | Change permissions |
| `hdfs dfs -chown student1:hadoop /user/student1` | Change owner/group |
| `hdfs dfs -setrep -w 3 /user/student1/file.txt` | Set replication factor to 3 |
| `hdfs dfs -stat %r /user/student1/file.txt` | Show replication factor |



## 2. HDFS Admin Commands (`hdfs dfsadmin`)


### 2.1 Cluster report
```
hdfs dfsadmin -report
```
**Sample output:**
```
Configured Capacity: 536870912000 (500 GB)
Present Capacity: 512000000000 (476.84 GB)
DFS Remaining: 480000000000 (447.03 GB)
DFS Used: 32000000000 (29.80 GB)
DFS Used%: 6.25%
Under replicated blocks: 0
Blocks with corrupt replicas: 0
Missing blocks: 0

-------------------------------------------------
Live datanodes (3):

Name: 192.168.1.11:9866 (datanode1)
Hostname: datanode1
Decommission Status : Normal
Configured Capacity: 178956970666 (166.67 GB)
DFS Used: 10666666666 (9.93 GB)
DFS Remaining: 168290304000 (156.73 GB)
DFS Used%: 5.96%
DFS Remaining%: 94.04%
...
```

### 2.2 Network topology (rack awareness)
```
hdfs dfsadmin -printTopology
```
**Sample output:**
```
Rack: /rack1
   192.168.1.11:9866 (datanode1)
   192.168.1.12:9866 (datanode2)

Rack: /rack2
   192.168.1.13:9866 (datanode3)
```
> Useful for teaching **rack awareness** — shows which DataNodes Hadoop
> believes are on which rack, based on the configured topology script
> (`net.topology.script.file.name` or `net.topology.table.file.name`).

### 2.3 Safe mode
NameNode enters **safe mode** on startup (read-only, no block replication/deletion)
until enough DataNodes report in.

| Command | Effect |
|---|---|
| `hdfs dfsadmin -safemode get` | Show current status |
| `hdfs dfsadmin -safemode enter` | Force NameNode into safe mode |
| `hdfs dfsadmin -safemode leave` | Force NameNode out of safe mode |
| `hdfs dfsadmin -safemode wait` | Block until safe mode is off |

**Sample:**
```
$ hdfs dfsadmin -safemode get
Safe mode is OFF

$ hdfs dfsadmin -safemode enter
Safe mode is ON

$ hdfs dfsadmin -safemode leave
Safe mode is OFF
```

### 2.4 Other common admin commands
| Command | Purpose |
|---|---|
| `hdfs dfsadmin -refreshNodes` | Reload include/exclude (decommission) node lists |
| `hdfs dfsadmin -saveNamespace` | Force NameNode to save current in-memory state to a new fsimage (must be in safe mode) |
| `hdfs dfsadmin -metasave meta.log` | Dump NameNode metadata state to a log file for diagnostics |
| `hdfs dfsadmin -setBalancerBandwidth 10485760` | Set bandwidth (bytes/sec) used by the balancer |
| `hdfs dfsadmin -finalizeUpgrade` | Finalize an HDFS version upgrade (removes rollback ability) |
| `hdfs dfsadmin -rollingUpgrade query` | Check status of a rolling upgrade |
| `hdfs dfsadmin -refreshServiceAcl` | Reload service-level authorization policy |
| `hdfs dfsadmin -listOpenFiles` | List files currently open for write |

**Sample: `-saveNamespace`**
```
$ hdfs dfsadmin -safemode enter
Safe mode is ON
$ hdfs dfsadmin -saveNamespace
Save namespace successful
$ hdfs dfsadmin -safemode leave
Safe mode is OFF
```

---

## 3. Filesystem Check — `fsck`
```
hdfs fsck / -files -blocks -locations
```
**Sample output:**
```
/user/student1/data.txt 215 bytes, 1 block(s):  OK
0. BP-123456789-192.168.1.10-1627849234:blk_1073741825_1001 len=215
   repl=3 [DatanodeInfoWithStorage[192.168.1.11:9866,...],
            DatanodeInfoWithStorage[192.168.1.12:9866,...],
            DatanodeInfoWithStorage[192.168.1.13:9866,...]]

Status: HEALTHY
Total size: 215 B
Total dirs: 2
Total files: 1
Total blocks (validated): 1 (avg. block size 215 B)
Minimally replicated blocks: 1 (100.0 %)
Over-replicated blocks: 0 (0.0 %)
Under-replicated blocks: 0 (0.0 %)
Corrupt blocks: 0
Number of data-nodes: 3
Number of racks: 2
```

---

## 4. FSImage & Edit Log — `oiv` and `oev`

These are **NameNode metadata files**, stored under
`${dfs.namenode.name.dir}/current/`:
- **fsimage_XXXXXX** — a checkpoint (snapshot) of the entire filesystem namespace at some transaction ID
- **edits_XXXXXX-YYYYYY** — the log of every namespace-modifying operation since the last checkpoint (played on top of fsimage to reconstruct current state)

### 4.1 OIV — Offline Image Viewer (reads `fsimage`)
```
hdfs oiv -i fsimage_0000000000000001234 -o fsimage.xml -p XML
```
Options for `-p` (processor): `XML`, `FileDistribution`, `Web`, `Delimited`, `ReverseXML`

**Sample: convert to XML**
```
$ hdfs oiv -i fsimage_0000000000000001234 -o fsimage.xml -p XML
$ head -c 400 fsimage.xml
<?xml version="1.0"?>
<fsimage>
  <NameSection>
    <namespaceId>123456789</namespaceId>
    <genstampV1>1000</genstampV1>
    ...
  <INodeSection>
    <inode>
      <id>16386</id>
      <type>DIRECTORY</type>
      <name></name>
      ...
```

**Sample: launch a browsable web view**
```
$ hdfs oiv -i fsimage_0000000000000001234 -o /dev/null -p Web -addr localhost:5978
```
Then browse `http://localhost:5978/webhdfs/v1/?op=LISTSTATUS` like a normal WebHDFS endpoint.

**Sample: FileDistribution (histogram of file sizes)**
```
$ hdfs oiv -i fsimage_0000000000000001234 -o dist.txt -p FileDistribution
Size	NumFiles
0	120
1024	45
65536	12
...
```

### 4.2 OEV — Offline Edits Viewer (reads `edits` log)
```
hdfs oev -i edits_0000000000000001235-0000000000000001240 -o edits.xml -p XML
```
Options for `-p`: `XML`, `Binary`, `Stats`

**Sample output (XML):**
```
$ head -c 500 edits.xml
<?xml version="1.0"?>
<EDITS>
  <EDITS_VERSION>-67</EDITS_VERSION>
  <RECORD>
    <OPCODE>OP_MKDIR</OPCODE>
    <DATA>
      <TXID>1236</TXID>
      <LENGTH>0</LENGTH>
      <INODEID>16400</INODEID>
      <PATH>/user/student1/logs</PATH>
      <TIMESTAMP>1753000000000</TIMESTAMP>
      ...
```

**Sample output (Stats mode — summary of op counts):**
```
$ hdfs oev -i edits_0000000000000001235-0000000000000001240 -o stats.txt -p Stats
$ cat stats.txt
VERSION                 : -67
OP_ADD                       (   0): 3
OP_MKDIR                     (   3): 2
OP_SET_REPLICATION           (   4): 1
OP_CLOSE                     (   9): 3
...
```

