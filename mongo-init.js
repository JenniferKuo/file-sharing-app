db = db.getSiblingDB('admin');
db.createUser({
  user: 'user1',
  pwd: 'password1',
  roles: [
    {
      role: 'readWrite',
      db: 'file_sharing',
    },
  ],
});

db = db.getSiblingDB('file_sharing');

db.createCollection('files');
db.createCollection('links');
